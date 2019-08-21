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

    private static CameraProvider instance = new CameraProvider();
    private Context context;
    private CameraRelatedDataHolder cameraRelatedDataHolder;

    private CameraProvider() {
    }

    public static CameraProvider getInstance(Context context) {
        instance.context = context;
        return instance;
    }

    public interface ImagePickerListener {
        void onImagePicked(Bitmap bitmapImage, String filepath);
    }

    public interface VideoPickerListener {
        void onVideoPicked(File file);
    }

    public void captureImage(boolean shouldCropImage, boolean shouldCropShapeOval, ImagePickerListener imagePickerListener) {
        cameraRelatedDataHolder = CameraRelatedDataHolder.getInstance();
        cameraRelatedDataHolder.setShouldCropImage(shouldCropImage);
        cameraRelatedDataHolder.setShouldCropShapeOval(shouldCropShapeOval);
        cameraRelatedDataHolder.setImagePickerListener(imagePickerListener);
        Intent intent = new Intent(context, CaptureImageActivity.class);
        context.startActivity(intent);
    }
}
