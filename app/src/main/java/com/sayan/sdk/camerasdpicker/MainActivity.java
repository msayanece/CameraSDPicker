package com.sayan.sdk.camerasdpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sayan.sdk.mediacollector.camerarelated.CameraProvider;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private CameraProvider cameraProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initialize CameraProvider & setup data
        cameraProvider = CameraProvider.getInstance(this);
        cameraProvider.setupProviderForImage(false,
                true,
                new CameraProvider.ImagePickerListener() {
                    @Override
                    public void onImagePicked(Bitmap bitmapImage, String filepath) {
                        imageView.setImageBitmap(bitmapImage);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraProvider.releaseProviderData();
    }

    public void choosePic(View view) {
        cameraProvider.captureImage();
    }
}
