package com.example.chahinemnist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    TextView result, confidence;
    ImageView imageView;
    Button picture;

    final int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);

        picture.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");

            if (image != null) {
                // Show image in ImageView, preserving aspect ratio
                int width = image.getWidth();
                int height = image.getHeight();

                // Calculate the scaling factor to preserve aspect ratio
                float scale = Math.min((float) imageView.getWidth() / width, (float) imageView.getHeight() / height);
                int newWidth = Math.round(scale * width);
                int newHeight = Math.round(scale * height);

                // Create a scaled bitmap
                Bitmap displayImage = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
                imageView.setImageBitmap(displayImage);

                // Resize image for the model (224x224)
                Bitmap resizedImage = Bitmap.createScaledBitmap(image, imageSize, imageSize, true);
                classifyImage(resizedImage);
            } else {
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Convert Bitmap to ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.f); // R
                    byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.f);  // G
                    byteBuffer.putFloat((val & 0xFF) / 255.f);         // B
                }
            }

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidences = outputFeature0.getFloatArray();

            // Find max confidence
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
            result.setText(classes[maxPos]);

            StringBuilder confidenceText = new StringBuilder();
            for (int i = 0; i < classes.length; i++) {
                confidenceText.append(String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100));
            }
            confidence.setText(confidenceText.toString());

            model.close();

        } catch (IOException e) {
            e.printStackTrace();
            result.setText("Model error");
        }
    }
}
