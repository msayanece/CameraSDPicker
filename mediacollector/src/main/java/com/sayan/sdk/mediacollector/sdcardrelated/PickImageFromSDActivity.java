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

import com.sayan.sdk.mediacollector.utils.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;

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
            finish();
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
//        if (data != null) {
//            try {
//                Uri selectedImage = data.getData();
//                dumpImageMetaData(selectedImage);
//                if (selectedImage != null) {
//                    bitmapImage = FileUtils.retrieveBitmapFromFileURI(this, selectedImage, 200, 200);
//                }
//                if (!wantToCrop) {
//                    listener.onGetBitmap(bitmapImage, displayName);
//                    finish();
//                } else {
//                    showImageCroperActivity(data, isOval);
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                finish();
//            }
//        }
    }

    /**
     * Returns the image result from CropActivity
     * @param result the result data
     */
    private void getImageFromCropActivity(CropImage.ActivityResult result) {
//        if (result != null) {
//            try {
//                Uri selectedImage = result.getUri();
//                if (selectedImage != null) {
//                    bitmapImage = FileUtils.retrieveBitmapFromFileURI(this, selectedImage, 200, 200);
//                }
//                if (listener != null) {
////                        dumpImageMetaData(selectedImage);
//                    listener.onGetBitmap(bitmapImage, displayName);
//                } else {
////                        dumpImageMetaData(selectedImage);
//                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
//                    dumpImageMetaDataMulti(selectedImage);
//                    bitmaps.add(bitmapImage);
//                    if (listenerMulti != null) {
//                        listenerMulti.onGetBitmap(bitmaps, displayNameMulti);
//                    }
//                }
//                this.finish();
//            } catch (IOException e) {
//                e.printStackTrace();
//                this.finish();
//            }
//        } else {
//            finish();
//        }

    }

    //<editor-fold desc="finish this activity">
    private void clearNFinishActivity() {
//        CameraProvider.getInstance().onDestroy();
        finish();
    }
    //</editor-fold>

}
