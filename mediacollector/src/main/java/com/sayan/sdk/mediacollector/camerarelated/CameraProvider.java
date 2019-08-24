package com.sayan.sdk.mediacollector.camerarelated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.sayan.sdk.mediacollector.CameraPicProvider;
import com.sayan.sdk.mediacollector.exceptions.CameraProviderSetupException;

import java.io.File;

/**
 * <p>Generic Plain java class which help to fetch image as a bitmap taking from camera
 * without the need to setting all the permission request call backs or onActivityResults
 * from the hosting activity.</p>
 */
public class CameraProvider {

    //<editor-fold desc="listeners">
    public interface ImagePickerListener {
        void onImagePicked(Bitmap bitmapImage, String filepath);
    }

    public interface VideoPickerListener {
        void onVideoPicked(File file);
    }
    //</editor-fold>

    private static CameraProvider instance = new CameraProvider();

    //<editor-fold desc="properties">
    private Context context;
    private boolean shouldCropImage;
    private boolean shouldCropShapeOval;
    private ImagePickerListener imagePickerListener;
    //</editor-fold>

    //<editor-fold desc="constructor">
    private CameraProvider() {
    }
    //</editor-fold>

    //<editor-fold desc="getInstances">
    public static CameraProvider getInstance(Context context) {
        instance.context = context;
        return instance;
    }

    static CameraProvider getInstance() {
        return instance;
    }
    //</editor-fold>

    //<editor-fold desc="getters">
    Context getContext() {
        return context;
    }

    boolean isShouldCropImage() {
        return shouldCropImage;
    }

    boolean isShouldCropShapeOval() {
        return shouldCropShapeOval;
    }

    ImagePickerListener getImagePickerListener() {
        return imagePickerListener;
    }
    //</editor-fold>

    //<editor-fold desc="setters">
    void setContext(Context context) {
        this.context = context;
    }

    void setShouldCropImage(boolean shouldCropImage) {
        this.shouldCropImage = shouldCropImage;
    }

    void setShouldCropShapeOval(boolean shouldCropShapeOval) {
        this.shouldCropShapeOval = shouldCropShapeOval;
    }

    void setImagePickerListener(ImagePickerListener imagePickerListener) {
        this.imagePickerListener = imagePickerListener;
    }
    //</editor-fold>

    //<editor-fold desc="capturing image">
    public void setupProviderForImage(boolean shouldCropImage, boolean shouldCropShapeOval, ImagePickerListener imagePickerListener) {
        this.shouldCropImage = shouldCropImage;
        this.shouldCropShapeOval = shouldCropShapeOval;
        this.imagePickerListener = imagePickerListener;
    }

    public void captureImage() {
        if (imagePickerListener == null) throw new CameraProviderSetupException("imagePickerListener not set.");
        if (context == null) throw new CameraProviderSetupException("Context is not set. " +
                "(Use Activity onStart() method for initialize & setup CameraProvider)");
        Intent intent = new Intent(context, CaptureImageActivity.class);
        context.startActivity(intent);
    }
    //</editor-fold>

    public void releaseProviderData(){
        context = null;
        shouldCropImage = false;
        shouldCropShapeOval = false;
        imagePickerListener = null;
    }
}
