package com.sayan.sdk.camerasdpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.sayan.sdk.mediacollector.camerarelated.CameraProvider;
import com.sayan.sdk.mediacollector.sdcardrelated.SDCardProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private SDCardProvider sdCardProvider;
    private ImageView imageView;
    private CameraProvider cameraProvider;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        videoView = findViewById(R.id.video);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*//initialize CameraProvider & setup data for Image
        cameraProvider = CameraProvider.getInstance(this);
        cameraProvider.setupProviderForImage(true,
                true,
                new CameraProvider.ImagePickerListener() {
                    @Override
                    public void onImagePicked(Bitmap bitmapImage, String filepath) {
                        imageView.setImageBitmap(bitmapImage);
                    }
                });*/

        /*
        sdCardProvider = SDCardProvider.getInstance(this);
        sdCardProvider.setupProviderForImage(true, false, new SDCardProvider.ImagePickerListener() {
            @Override
            public void onImagePicked(Bitmap bitmap, String imagePath) {
                imageView.setImageBitmap(bitmap);
            }
        });*/

        /*sdCardProvider = SDCardProvider.getInstance(this);
        sdCardProvider.setupProviderForVideo(new SDCardProvider.VideoPickerListener() {
            @Override
            public void onVideoPicked(File file) {
                 videoView.setVideoPath(file.getAbsolutePath());
                 videoView.start();
            }
        });*/

        sdCardProvider = SDCardProvider.getInstance(this);
        sdCardProvider.setupProviderForAnyFile(new SDCardProvider.AnyFilePickerListener() {
            @Override
            public void onFilePicked(File file) {
                Toast.makeText(MainActivity.this, "File: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
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
//        cameraProvider.releaseProviderData();
    }

    public void choosePic(View view) {
        sdCardProvider.pickAnyFile();
//        cameraProvider.captureImage();
    }
}
