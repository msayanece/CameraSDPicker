package com.sayan.sdk.camerasdpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sayan.sdk.mediacollector.sdcardrelated.SDCardProvider;

public class MainActivity extends AppCompatActivity {
    private SDCardProvider sdCardProvider;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*//initialize CameraProvider & setup data for Image
        cameraProvider = CameraProvider.getInstance(this);
        cameraProvider.setupProviderForImage(false,
                true,
                new CameraProvider.ImagePickerListener() {
                    @Override
                    public void onImagePicked(Bitmap bitmapImage, String filepath) {
                        imageView.setImageBitmap(bitmapImage);
                    }
                });*/

        //initialize CameraProvider & setup data for Video
        sdCardProvider = SDCardProvider.getInstance(this);
        sdCardProvider.setupProviderForImage(false, false, new SDCardProvider.ImagePickerListener() {
            @Override
            public void onImagePicked(Bitmap bitmap, String imagePath) {
                imageView.setImageBitmap(bitmap);
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
        sdCardProvider.releaseProviderData();
    }

    public void choosePic(View view) {
        sdCardProvider.pickImage();
    }
}
