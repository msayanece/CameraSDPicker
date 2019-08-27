package com.sayan.sdk.camerasdpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.sayan.sdk.mediacollector.camerarelated.CameraProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private CameraProvider cameraProvider;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.video);
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
        cameraProvider = CameraProvider.getInstance(this);
        cameraProvider.setupProviderForVideo(new CameraProvider.VideoPickerListener() {
            @Override
            public void onVideoPicked(File file) {
                videoView.setVideoPath(file.getAbsolutePath());
                videoView.start();
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
        cameraProvider.captureVideo();
    }
}
