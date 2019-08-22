package com.sayan.sdk.mediacollector.camerarelated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.sayan.sdk.mediacollector.CameraPicProvider;

import java.io.File;

/**
 * <p>Generic Plain java class which help to fetch image as a bitmap taking from camera
 * without the need to setting all the permission request call backs or onActivityResults
 * from the hosting activity.</p>
 */
public class CameraProvider {

    public interface ImagePickerListener {
        void onImagePicked(Bitmap bitmapImage, String filepath);
    }

    public interface VideoPickerListener {
        void onVideoPicked(File file);
    }

    private static CameraProvider instance = new CameraProvider();
    private Context context;
    private boolean shouldCropImage;
    private boolean shouldCropShapeOval;
    private ImagePickerListener imagePickerListener;

    private CameraProvider() {
    }

    public static CameraProvider getInstance(Context context) {
        instance.context = context;
        return instance;
    }

    static CameraProvider getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public boolean isShouldCropImage() {
        return shouldCropImage;
    }

    public boolean isShouldCropShapeOval() {
        return shouldCropShapeOval;
    }

    public ImagePickerListener getImagePickerListener() {
        return imagePickerListener;
    }

    public void captureImage(boolean shouldCropImage, boolean shouldCropShapeOval, ImagePickerListener imagePickerListener) {
        this.shouldCropImage = shouldCropImage;
        this.shouldCropShapeOval = shouldCropShapeOval;
        this.imagePickerListener = imagePickerListener;
        Intent intent = new Intent(context, CaptureImageActivity.class);
        context.startActivity(intent);
    }

    void onDestroy(){
        context = null;
        shouldCropImage = false;
        shouldCropShapeOval = false;
        imagePickerListener = null;
    }
}
