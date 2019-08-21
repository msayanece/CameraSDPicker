package com.sayan.sdk.mediacollector.camerarelated;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.sayan.sdk.mediacollector.R;

public class CaptureImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lockScreenOrientationToCurrent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
    }

    @Override
    protected void onStop() {
        unlockScreenOrientationToCurrent();
        super.onStop();
    }

    private void lockScreenOrientationToCurrent() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void unlockScreenOrientationToCurrent() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
