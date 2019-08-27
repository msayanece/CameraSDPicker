package com.sayan.sdk.mediacollector.camerarelated;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.sayan.sdk.mediacollector.utils.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.PERMISSION_REQUEST_FOR_CAMERA;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.PERMISSION_REQUEST_FOR_EXTERNAL_STORAGE;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.REQUEST_CAMERA_INTENT;
import static com.sayan.sdk.mediacollector.camerarelated.CameraConstants.REQUEST_VIDEO_INTENT;
import static com.sayan.sdk.mediacollector.utils.FileUtils.getFile;

public class CaptureVideoActivity extends Activity {

    private static final String TAG = "CaptureVideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //lock the orientation
        lockScreenOrientationToCurrent();
        super.onCreate(savedInstanceState);
        requestPermissionForExternalStorage();

        //check if activity recreated or first time created
//        if (savedInstanceState == null) {
//            //first time
//        } else {
//            //recreated
//            mCapturedImageFileURI = Uri.parse(savedInstanceState.getString("mCapturedImageFileURI"));
//        }
    }

    //<editor-fold desc="request permission for External Storage & Camera">

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
    //</editor-fold>


    /**
     * Start the implicit intent
     */
    private void cameraIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        /*Uri fileURI = null;
        if (Build.VERSION.SDK_INT >= 24) {
            File nFile = getFile();
            mCapturedImageFileURI = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider", nFile);
        } else {
            //getting uri of the file
            mCapturedImageFileURI = Uri.fromFile(getFile());
        }
        takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mCapturedImageFileURI);*/
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_INTENT);
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
            if (requestCode == REQUEST_VIDEO_INTENT) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onCaptureVideoResult(data);
                    }
                }, 200);
            }
        } else if (resultCode == RESULT_CANCELED) {
            //User canceled the camera capturing activity
            Toast.makeText(this, "Video capturing canceled", Toast.LENGTH_SHORT).show();
            clearNFinishActivity();
        }
    }

    /**
     * After capturing the video result it process the file and send using file listener callback
     *
     * @param data
     */
    private void onCaptureVideoResult(Intent data) {
        //fetch the selected video URI
        Uri selectedVideo = data.getData();
        File file = null;
        //create the cursor to query the selected video
        String[] filePathColumn = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedVideo,
                filePathColumn, null, null, null);
        if (cursor != null) {
            //the first indexed string is our decodable file string
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String decodableString = cursor.getString(columnIndex);
            cursor.close();
            //create the file using that string
            file = new File(decodableString);
        }
        //check if file is fetched successfully and add it to the callback
        if (file != null) {
            CameraProvider.getInstance().getVideoPickerListener().onVideoPicked(file);
            clearNFinishActivity();
        } else {
            clearNFinishActivity();
        }

    }

    //<editor-fold desc="finish this activity">
    private void clearNFinishActivity() {
//        CameraProvider.getInstance().onDestroy();
        unlockScreenOrientationToCurrent();
        finish();
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
