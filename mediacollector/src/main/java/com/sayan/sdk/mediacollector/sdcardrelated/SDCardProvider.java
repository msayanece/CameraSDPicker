package com.sayan.sdk.mediacollector.sdcardrelated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.sayan.sdk.mediacollector.camerarelated.CaptureImageActivity;
import com.sayan.sdk.mediacollector.camerarelated.CaptureVideoActivity;
import com.sayan.sdk.mediacollector.exceptions.CameraProviderSetupException;

/**
 * <p>Generic Plain java class which help to fetch image as a bitmap taking from camera
 * without the need to setting all the permission request call backs or onActivityResults
 * from the hosting activity.</p>
 */
public class SDCardProvider {

    //<editor-fold desc="listeners">
    public interface ImagePickerListener {
        void onImagePicked(Bitmap bitmap, String imagePath);
    }

//    public interface GetVideoFileListener {
//        void onGetFile(File file);
//    }
//
//    public interface GetAnyFileListener {
//        void onGetFile(File file);
//    }
//
//    public interface GetBitmapListenerMulti {
//        void onGetBitmap(List<Bitmap> bitmaps, List<String> imagePaths);
//    }
//
//    public interface GetVideoFileListenerMulti {
//        void onGetFile(List<File> files);
//    }
//
//    public interface GetAnyFileListenerMulti {
//        void onGetFile(List<File> files);
//    }

    //</editor-fold>

    private static SDCardProvider instance = new SDCardProvider();

    //<editor-fold desc="properties">
    private Context context;
    //Image
    private boolean shouldCropImage;
    private boolean shouldCropShapeOval;
    private ImagePickerListener imagePickerListener;

    //Video

    //</editor-fold>

    //<editor-fold desc="constructor">
    private SDCardProvider() {
    }
    //</editor-fold>

    //<editor-fold desc="getInstances">
    public static SDCardProvider getInstance(Context context) {
        instance.context = context;
        return instance;
    }

    static SDCardProvider getInstance() {
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

    public void pickImage() {
        if (imagePickerListener == null) throw new CameraProviderSetupException("imagePickerListener not set.");
        if (context == null) throw new CameraProviderSetupException("Context is not set. " +
                "(Use Activity onStart() method to initialize & setup CameraProvider)");
        Intent intent = new Intent(context, PickImageFromSDActivity.class);
        context.startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold desc="capturing video">
//    public void setupProviderForVideo(VideoPickerListener videoPickerListener) {
//        this.videoPickerListener = videoPickerListener;
//    }
//
//    public void captureVideo() {
//        if (videoPickerListener == null) throw new CameraProviderSetupException("videoPickerListener not set.");
//        if (context == null) throw new CameraProviderSetupException("Context is not set. " +
//                "(Use Activity onStart() method to initialize & setup CameraProvider)");
//        Intent intent = new Intent(context, CaptureVideoActivity.class);
//        context.startActivity(intent);
//    }
    //</editor-fold>

    public void releaseProviderData(){
        context = null;
        shouldCropImage = false;
        shouldCropShapeOval = false;
        imagePickerListener = null;
//        videoPickerListener = null;
    }
}
