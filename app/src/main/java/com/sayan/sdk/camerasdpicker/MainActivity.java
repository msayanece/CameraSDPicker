package com.sayan.sdk.camerasdpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sayan.sdk.mediacollector.CameraPicProvider;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
    }

    public void choosePic(View view) {
        new CameraPicProvider(this, true, false, new CameraPicProvider.GetBitmapListener() {
            @Override
            public void onGetBitmap(Bitmap bitmapImage, String filepath) {
                Toast.makeText(MainActivity.this, "Image picked successfully", Toast.LENGTH_SHORT).show();
                imageView.setImageBitmap(bitmapImage);
            }
        });
    }
}
