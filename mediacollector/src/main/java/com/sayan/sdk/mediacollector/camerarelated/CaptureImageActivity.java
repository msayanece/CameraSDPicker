package com.sayan.sdk.mediacollector.camerarelated;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.sayan.sdk.mediacollector.utils.FileUtils;

import java.io.File;

import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.PERMISSION_REQUEST_FOR_CAMERA;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.REQUEST_CAMERA_INTENT;

public class CaptureImageActivity extends Activity {
    private static final String TAG = "CaptureImageActivity";

    //<editor-fold desc="properties">
    private Uri mCapturedImageFileURI;
    private String mCurrentPhotoPath;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //lock the orientation
        lockScreenOrientationToCurrent();
        super.onCreate(savedInstanceState);
        //check if activity recreated or first time created
        if (savedInstanceState == null) {
            //first time
            requestPermissionForExternalStorage();
        } else {
            //recreated
            mCapturedImageFileURI = Uri.parse(savedInstanceState.getString("mCapturedImageFileURI"));
            mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
        }
    }

    //region request permission for External Storage & Camera
    /**
     * Request for external storage permission
     */
    private boolean requestPermissionForExternalStorage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Toast.makeText(getApplicationContext(), "External storage permission is mandatory",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE);
            }
            return true;
        } else {
            requestPermissionForCamera();
            return false;
        }
    }

    /**
     * Request for camera permission
     */
    public boolean requestPermissionForCamera() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //Toast.makeText(getApplicationContext(), "External storage permission is mandatory",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_FOR_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_FOR_CAMERA);
            }
            return true;
        } else {
            cameraIntent();
            return false;
        }
    }

    /**
     * Permission Request result callback
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
//                    Toast.makeText(getApplicationContext(), "SMS Permission granted", Toast.LENGTH_LONG).show();
                    requestPermissionForCamera();
                } else {
//                    Toast.makeText(getApplicationContext(), "",Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
                    clearNFinishActivity();
                }
                break;
            }

            case PERMISSION_REQUEST_FOR_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
//                    Toast.makeText(getApplicationContext(), "SMS Permission granted", Toast.LENGTH_LONG).show();
                        cameraIntent();
                } else {
//                    Toast.makeText(getApplicationContext(), "",Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
                    clearNFinishActivity();
                }
                break;
            }
        }
    }
    //endregion

    /**
     * Start the implicit intent
     */
    private void cameraIntent() {
        File file = FileUtils.getFile();
        //getting file path of the mCurrentPhotoPath
        mCurrentPhotoPath = file.getAbsolutePath();
        //getting uri of the mCapturedImageFileURI
        if (Build.VERSION.SDK_INT >= 24) {
            //for nougat
            mCapturedImageFileURI = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider", file);
        } else {
            //before nougat
            mCapturedImageFileURI = Uri.fromFile(file);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageFileURI);
        //check if there is any camera app available and then start intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA_INTENT);
        } else {
            Toast.makeText(this, "There is no Camera Application found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d(TAG, "onActivityResult: called");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: RESULT_OK");
            if (requestCode == REQUEST_CAMERA_INTENT) {
                //camera activity was started by REQUEST_CAMERA_INTENT
                Log.d(TAG, "onActivityResult: REQUEST_CAMERA_INTENT");
                onCaptureImageResult();
            }
            //TODO uncomment
//            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //camera activity was started by CROP_IMAGE_ACTIVITY_REQUEST_CODE
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                if (resultCode == RESULT_OK) {
//                    getImageFromCropActivity(result);
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Exception error = result.getError();
//                    clearNFinishActivity();
//                }
//            }
        } else if (resultCode == RESULT_CANCELED) {
            //User canceled the camera capturing activity
            Toast.makeText(this, "Image capturing canceled", Toast.LENGTH_SHORT).show();
            clearNFinishActivity();
        }
    }

    private void onCaptureImageResult() {
        try {
            //declare the Bitmap variable & get the CameraProvider instance without context
            // (We don't need to reinitialize the context)
            Log.d(TAG, "onCaptureImageResult: called");
            Bitmap bitmapImage = null;
            CameraProvider cameraProvider = CameraProvider.getInstance();
            if (mCapturedImageFileURI != null) {
                //everything is fine, retrieve the bitmap image from the URI
                Log.d(TAG, "onCaptureImageResult: mCapturedImageFileURI not null");
                bitmapImage = FileUtils.retrieveBitmapFromFileURI(
                        this, mCapturedImageFileURI, 200, 200
                );
            }
            if (!cameraProvider.isShouldCropImage()) {
                //no need to crop image send data through the listener & finish activity
                cameraProvider.getImagePickerListener().onImagePicked(bitmapImage, mCurrentPhotoPath);
                clearNFinishActivity();
            } else {
                //open crop image activity
                //TODO uncomment
//                showImageCropperActivity(isOval);
            }
        } catch (Exception e) {
            //something went wrong, finish this activity so that the actual visible activity can again gain control
            e.printStackTrace();
            clearNFinishActivity();
        }
    }

    //region finish this activity
    private void clearNFinishActivity() {
//        CameraProvider.getInstance().onDestroy();
        unlockScreenOrientationToCurrent();
        finish();
    }
    //endregion


    //<editor-fold desc="onRestore & onSave InstanceState">
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //null check not required, but for safety
        if (savedInstanceState != null) {
            //activity restored, restore date
            mCapturedImageFileURI = Uri.parse(savedInstanceState.getString("mCapturedImageFileURI"));
            mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
        }
        Log.d(TAG, "onRestoreInstanceState: called - mCapturedImageFileURI: "
                + mCapturedImageFileURI + ", mCurrentPhotoPath: " + mCurrentPhotoPath);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //activity is going to destroy save data
        Log.d(TAG, "onSaveInstanceState: called & saved - mCapturedImageFileURI: "
                + mCapturedImageFileURI + ", mCurrentPhotoPath: " + mCurrentPhotoPath);
        if (outState != null) {
            if (mCapturedImageFileURI != null) {
                outState.putString("mCapturedImageFileURI", mCapturedImageFileURI.toString());
            }
            if (mCurrentPhotoPath != null) {
                outState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
            }
        }
        super.onSaveInstanceState(outState);
    }
    //</editor-fold>


    //<editor-fold desc="lock/ unlock orientation">
    /**
     * method to lock orientation to current
     */
    private void lockScreenOrientationToCurrent() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * go back to previous orientation settings as per the application app module
     */
    private void unlockScreenOrientationToCurrent() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
    //</editor-fold>

}
