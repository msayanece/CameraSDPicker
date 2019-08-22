package com.sayan.sdk.camerasdpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sayan.sdk.mediacollector.CameraPicProvider;
import com.sayan.sdk.mediacollector.SdCardPicProvider;
import com.sayan.sdk.mediacollector.camerarelated.CameraProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
    }

    public void choosePic(View view) {
        CameraProvider.getInstance(this).captureImage(
                false,
                false,
                new CameraProvider.ImagePickerListener() {
                    @Override
                    public void onImagePicked(Bitmap bitmapImage, String filepath) {
                        imageView.setImageBitmap(bitmapImage);
                    }
                });
    }
}
