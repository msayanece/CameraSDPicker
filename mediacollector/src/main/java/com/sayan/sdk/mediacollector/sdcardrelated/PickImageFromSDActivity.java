package com.sayan.sdk.mediacollector.sdcardrelated;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sayan.sdk.mediacollector.R;
import com.sayan.sdk.mediacollector.utils.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;

import static com.sayan.sdk.mediacollector.sdcardrelated.SDCardConstants.REQUEST_FOR_READ_EXTERNAL_STORAGE;
import static com.sayan.sdk.mediacollector.sdcardrelated.SDCardConstants.SELECT_IMAGE_INTENT;

public class PickImageFromSDActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            requestPermissionForReadExternalStorage();
        } else {

        }
    }

    private boolean requestPermissionForReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Toast.makeText(getApplicationContext(), "External storage permission is mandatory",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_READ_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_READ_EXTERNAL_STORAGE);
            }
            return true;
        } else {
            imageFileChooserIntent();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    imageFileChooserIntent();
                } else {
                    Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
                    clearNFinishActivity();
                }
                break;
            }
        }
    }

    private void imageFileChooserIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE_INTENT:
                    onSelectFromGalleryResult(data);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    getImageFromCropActivity(result);
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            clearNFinishActivity();
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Uri selectedImage = data.getData();
                String displayName = FileUtils.dumpImageMetaData(this, selectedImage);
                Bitmap bitmapImage = null;
                if (selectedImage != null) {
                    bitmapImage = FileUtils.retrieveBitmapFromFileURI(this, selectedImage, 200, 200);
                }
                if (!SDCardProvider.getInstance().isShouldCropImage()) {
                    SDCardProvider.getInstance().getImagePickerListener().onImagePicked(bitmapImage, displayName);
                    finish();
                } else {
                    showImageCropperActivity(data, SDCardProvider.getInstance().isShouldCropShapeOval());
                }
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    /**
     * Shows the image cropper activity where user can crop the picked image in oval shape or
     * rectangle shape
     *
     * @param data   the picture data
     * @param isOval crop as oval or rectangle
     */
    private void showImageCropperActivity(Intent data, boolean isOval) {
        if (data != null) {
            Uri selectedImage = data.getData();
            CropImage.activity(selectedImage)
                    .setCropShape(isOval ? CropImageView.CropShape.OVAL : CropImageView.CropShape.RECTANGLE)
                    .setActivityMenuIconColor(getResources().getColor(android.R.color.white))
                    .setBorderCornerColor(getResources().getColor(android.R.color.darker_gray))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
    }

    /**
     * Returns the image result from CropActivity
     *
     * @param result the result data
     */
    private void getImageFromCropActivity(CropImage.ActivityResult result) {
        if (result != null) {
            try {
                Uri selectedImage = result.getUri();
                Bitmap bitmapImage = null;
                SDCardProvider.ImagePickerListener imagePickerListener = SDCardProvider.getInstance().getImagePickerListener();
                if (selectedImage != null) {
                    bitmapImage = FileUtils.retrieveBitmapFromFileURI(this, selectedImage, 200, 200);
                }
                if (imagePickerListener != null) {
                    String displayName = FileUtils.dumpImageMetaData(this, selectedImage);
                    imagePickerListener.onImagePicked(bitmapImage, displayName);
                }
                clearNFinishActivity();
            } catch (IOException e) {
                e.printStackTrace();
                clearNFinishActivity();
            }
        } else {
            clearNFinishActivity();
        }
    }

    //<editor-fold desc="finish this activity">
    private void clearNFinishActivity() {
//        CameraProvider.getInstance().onDestroy();
        finish();
    }
    //</editor-fold>

}
