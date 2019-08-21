package com.sayan.sdk.mediacollector.camerarelated;

public class CameraRelatedDataHolder {
    private static final CameraRelatedDataHolder ourInstance = new CameraRelatedDataHolder();

    private boolean shouldCropImage;
    private boolean shouldCropShapeOval;
    private CameraProvider.ImagePickerListener imagePickerListener;

    public static CameraRelatedDataHolder getInstance() {
        return ourInstance;
    }

    private CameraRelatedDataHolder() {
    }

    public boolean isShouldCropImage() {
        return shouldCropImage;
    }

    public void setShouldCropImage(boolean shouldCropImage) {
        this.shouldCropImage = shouldCropImage;
    }

    public boolean isShouldCropShapeOval() {
        return shouldCropShapeOval;
    }

    public void setShouldCropShapeOval(boolean shouldCropShapeOval) {
        this.shouldCropShapeOval = shouldCropShapeOval;
    }

    public CameraProvider.ImagePickerListener getImagePickerListener() {
        return imagePickerListener;
    }

    public void setImagePickerListener(CameraProvider.ImagePickerListener imagePickerListener) {
        this.imagePickerListener = imagePickerListener;
    }
}
