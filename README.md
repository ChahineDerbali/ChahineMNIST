# ChahineMNIST

ChahineMNIST is an Android application that uses a custom-trained **TensorFlow Lite** model to recognize handwritten digits (0–9) from images taken with the device’s camera. The model is trained on my own handwriting using **Google's Teachable Machine**, making it personalized and accurate for my digit style.

## 📱 Features

- Capture handwritten digits with your phone camera
- Real-time digit prediction using on-device ML
- Model trained on custom handwriting samples
- Lightweight and fast thanks to TensorFlow Lite

## 🎥 Based On

This project was originally inspired by the tutorial from **IJ Apps**:  
[👉 Watch the video here](https://www.youtube.com/watch?v=yV9nrRIC_R0&t=11s)

I customized the project by training my own dataset and integrating it into the Android app.

## 🛠 Built With

- Android Studio
- Java *(or Kotlin — specify which one you used)*
- TensorFlow Lite
- Google Teachable Machine
- CameraX API *(if used)*

## 📂 Project Structure

```
ChahineMNIST/
│
├── app/
│   ├── src/
│   ├── tflite_model.tflite  <-- Your trained model
│   └── ...
├── .gitignore
├── README.md
└── ...
```

## 🚀 Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/ChahineMNIST.git
   ```
2. Open with Android Studio
3. Connect your Android device or use an emulator
4. Run the app and test by taking a photo of handwritten digits (0–9)

---

Made with ❤️ by [Chahine Derbali](https://github.com/your-username)
