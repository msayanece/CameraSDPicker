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
import android.widget.Toast;

import com.sayan.sdk.mediacollector.utils.FileUtils;

import java.io.File;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.PERMISSION_REQUEST_FOR_CAMERA;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.REQUEST_CAMERA_INTENT;

public class CaptureImageActivity extends Activity {

    private Uri mCapturedImageFileURI;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lockScreenOrientationToCurrent();
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            requestPermissionForExternalStorage();
        } else {
            mCapturedImageFileURI = Uri.parse(savedInstanceState.getString("mCapturedImageFileURI"));
            mCurrentPhotoPath = savedInstanceState.getString("path");
        }
    }

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

    /**
     * Start the implicit intent
     */
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //for nougat
        if (Build.VERSION.SDK_INT >= 24) {
            File file = FileUtils.getFile();
            mCurrentPhotoPath = file.getAbsolutePath();
            mCapturedImageFileURI = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider", file);
        } else {
            //getting uri of the mCapturedImageFileURI
            mCapturedImageFileURI = Uri.fromFile(FileUtils.getFile());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageFileURI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA_INTENT);
        } else {
            Toast.makeText(this, "There is no Camera Application found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_INTENT) {
                onCaptureImageResult(data);
            }
            //TODO uncomment
//            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                if (resultCode == RESULT_OK) {
//                    getImageFromCropActivity(result);
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Exception error = result.getError();
//                    finish();
//                }
//            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Image capturing canceled", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap bitmapImage = null;
            CameraProvider cameraProvider = CameraProvider.getInstance();
            if (mCapturedImageFileURI != null) {
                bitmapImage = FileUtils.retrieveBitmapFromFileURI(this, mCapturedImageFileURI, 200, 200);
            }
            if (!cameraProvider.isShouldCropImage()) {
                cameraProvider.getImagePickerListener().onImagePicked(bitmapImage, mCurrentPhotoPath);
                clearNFinishActivity();
            } else {
                //TODO uncomment
//                showImageCropperActivity(isOval);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void clearNFinishActivity() {
        finish();
    }


    @Override
    protected void onStop() {
        unlockScreenOrientationToCurrent();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        CameraProvider.getInstance().onDestroy();
        super.onDestroy();
    }

    private void lockScreenOrientationToCurrent() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void unlockScreenOrientationToCurrent() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
