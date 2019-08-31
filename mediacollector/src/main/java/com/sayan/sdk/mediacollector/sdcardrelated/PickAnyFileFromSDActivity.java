package com.sayan.sdk.mediacollector.sdcardrelated;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sayan.sdk.mediacollector.utils.FileUtils;

import java.io.File;

import static com.sayan.sdk.mediacollector.sdcardrelated.SDCardConstants.REQUEST_FOR_READ_EXTERNAL_STORAGE;
import static com.sayan.sdk.mediacollector.sdcardrelated.SDCardConstants.SELECT_ANY_INTENT;
import static com.sayan.sdk.mediacollector.sdcardrelated.SDCardConstants.SELECT_VIDEO_INTENT;
import static com.sayan.sdk.mediacollector.utils.FileUtils.getFile;

public class PickAnyFileFromSDActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionForReadExternalStorage();
    }

    //<editor-fold desc="permission related">
    // External Storage permission
    private boolean requestPermissionForReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted yet
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // No rationale required in this case - Request permission
                //Toast.makeText(getApplicationContext(), "External storage permission is mandatory",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_READ_EXTERNAL_STORAGE);
            } else {
                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_READ_EXTERNAL_STORAGE);
            }
            return true;
        } else {
            //permission already granted
            anyFileChooserIntent();
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
                    anyFileChooserIntent();
                } else {
                    Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
                    clearNFinishActivity();
                }
                break;
            }
        }
    }
    //</editor-fold>

    /**
     * Open Gallery
     */
    private void anyFileChooserIntent() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);     //only local storage image allowed
        startActivityForResult(Intent.createChooser(intent, "Select file"), SELECT_ANY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_ANY_INTENT:
                    onSelectFromGalleryResult(data);
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            clearNFinishActivity();
        }
    }


    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            File file;
            Uri selectFile = data.getData();
            String displayName = FileUtils.dumpImageMetaData(this, selectFile);
            if (selectFile != null) {
                file = getFile(this, selectFile);
                SDCardProvider.AnyFilePickerListener anyFilePickerListener = SDCardProvider.getInstance().getAnyFilePickerListener();
                if (file == null) {
                    file = getFile(this, selectFile);
                }
                if (file != null) {
                    if (anyFilePickerListener != null) {
                        anyFilePickerListener.onFilePicked(file);
                    }
                }
            } else {
                Log.e("onVideoSelect ", "selected file not found");
            }
        }
        clearNFinishActivity();
    }


    //<editor-fold desc="finish this activity">
    private void clearNFinishActivity() {
//        CameraProvider.getInstance().onDestroy();
        finish();
    }
    //</editor-fold>
}
