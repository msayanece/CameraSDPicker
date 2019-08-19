package com.sayan.sdk.mediacollector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.sayan.sdk.mediacollector.utils.ResourceUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DCIM;

/**
 * <p>Generic Plain java class which help to fetch image as a bitmap taking from camera
 * without the need to setting all the permission request call backs or onActivityresults
 * from the hosting activity.</p>
 * <p>
 */


public class CameraPicProvider {

    private static boolean isOval;
    private Context context;
    private static GetBitmapListener listener;
    private static GetFileListener fileListener;
    private static boolean wantToCrop;

    /**
     * <p>
     * Add the following dependency in app level build gradle
     * </p>
     * {@code
     * implementation 'com.theartofdev.edmodo:android-image-cropper:2.7.+'
     * }
     * <p>
     * <p>
     * Add the following line of code in the manifest under the application tag
     * </p>
     * {@code
     * <activity
     * android:name=".camerapicchooser.CameraPicProvider$PermissionAndResultReciverActivity"
     * android:theme="@style/Theme.AppCompat.Translucent"/>
     * }
     * <p>
     * {@code
     * <activity
     * android:name="com.theartofdev.edmodo.cropper.CropImageActivity"
     * android:theme="@style/crop_imge_theme" />
     * }
     * </p>
     * <p>
     * Add the following permission in the manifest file
     * </p>
     * <p>
     * {@code
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.CAMERA" />
     * }
     * </p>
     * <p>
     * <p>
     * Add the following line of code in the style.xml
     * </p>
     * <p>
     * {@code
     * <style name="Theme.AppCompat.Translucent" parent="Theme.AppCompat.NoActionBar">
     * <item name="android:background">#33000000</item> <!-- Or any transparency or color you need -->
     * <item name="android:windowNoTitle">true</item>
     * <item name="android:windowBackground">@android:color/transparent</item>
     * <item name="android:colorBackgroundCacheHint">@null</item>
     * <item name="android:windowIsTranslucent">true</item>
     * <item name="android:windowAnimationStyle">@android:style/Animation</item>
     * </style>
     * }
     * <p>
     * <h3>How to use </h3>
     * {@code
     * new CameraPicProvider(CreatePostActivity.this,true, new CameraPicProvider.GetBitmapListener(){
     *
     * @param context    activity context
     *                   </p><p>
     * @param wantToCrop if true,This provider class open an image cropper activity screen
     *                   where you cancrop the picked image.
     *                   </p><p>
     * @param listener   listener for getting the bitmap image and the file path
     *                   </p>
     * @Override public void onGetBitmap(Bitmap bitmap, String imagePath) {
     * }
     * });
     * }
     * <p>
     * <p>
     * </p>
     */


    //for camera image
    public CameraPicProvider(Context context, boolean wantToCrop, boolean isOval, GetBitmapListener listener) {
        this.context = context;
        CameraPicProvider.isOval = isOval;
        CameraPicProvider.listener = listener;
        CameraPicProvider.wantToCrop = wantToCrop;
        openPermissionAndResultReciver(false);
    }

    //for camera video
    public CameraPicProvider(Context context, GetFileListener listener) {
        this.context = context;
        fileListener = listener;
        openPermissionAndResultReciver(true);
    }

    private void openPermissionAndResultReciver(boolean isVideo) {
        Intent intent = new Intent(context, PermissionAndResultReceiverActivity.class);
        intent.putExtra("isVideo", isVideo);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * <p>A static inner activity which is not visible to user its main pupose is to recive</p>
     * {@link #onRequestPermissionsResult(int, String[], int[])}
     * <p>and</p>
     * {@link #onActivityResult(int, int, Intent)}
     * <p>
     * <p>
     * it fetches the image bitmap internally and callback to the listener
     * </p>
     */

    public static class PermissionAndResultReceiverActivity extends Activity {
        public static final int SELECT_FILE = 0;
        private static final int REQUEST_FOR_EXTERNAL_STORAGE = 1001;
        public static final int REQUEST_FOR_CAMERA = 1002;
        public static final int REQUEST_VIDEO = 1003;
        private int REQUEST_CAMERA = 0;
        private Bitmap bitmapImage;
        private String mCurrentPhotoPath;
        private Uri file;
        private boolean isVideo;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            isVideo = getIntent().getBooleanExtra("isVideo", false);
            if (savedInstanceState == null) {
                requestPermissionForExternalStorage();
            } else {
                file = Uri.parse(savedInstanceState.getString("file"));
                mCurrentPhotoPath = savedInstanceState.getString("path");

            }
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            if (outState != null) {
                if (file != null) {
                    outState.putString("file", file.toString());
                }
                if (mCurrentPhotoPath != null) {
                    outState.putString("path", mCurrentPhotoPath);
                }
            }
            super.onSaveInstanceState(outState);
        }

      /*  @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            if (savedInstanceState!=null) {
                file = Uri.parse(savedInstanceState.getString("file"));
            }
        }*/

        public boolean requestPermissionForExternalStorage() {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
// explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//Toast.makeText(getApplicationContext(), "External storage permission is mandatory",Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_FOR_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_FOR_EXTERNAL_STORAGE);
                }
                return true;
            } else {
                requestPermissionForCamera();
                return false;
            }
        }

        /**
         * Request for camera permission
         * @return
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
                            REQUEST_FOR_CAMERA);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_FOR_CAMERA);
                }
                return true;
            } else {
                if (isVideo) {
                    videoIntent();
                } else {
                    cameraIntent();
                }
                return false;
            }
        }

        private void videoIntent() {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
           /* if (Build.VERSION.SDK_INT >= 24) {
                File nFile = getFile();
                file = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider", nFile);
            } else {
                //getting uri of the file
                file = Uri.fromFile(getFile());
            }
            takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, file);*/
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO);
            } else {
                Toast.makeText(this, "There is no Camera Application found", Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * Start the implicit intent
         */
        private void cameraIntent() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //for nougat
            if (Build.VERSION.SDK_INT >= 24) {
                File nFile = getFile();
                file = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider", nFile);
            } else {
                //getting uri of the file
                file = Uri.fromFile(getFile());
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "There is no Camera Application found", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * returns the file
         * @return
         */
        private File getFile() {

            File folder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);// the file path

            //if it doesn't exist the folder will be created
            if (!folder.exists()) {
                folder.mkdir();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File image_file = null;

            try {
                image_file = File.createTempFile(imageFileName, ".jpeg", folder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCurrentPhotoPath = image_file.getAbsolutePath();
            return image_file;
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
                case REQUEST_FOR_EXTERNAL_STORAGE: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
//                    Toast.makeText(getApplicationContext(), "SMS Permission granted", Toast.LENGTH_LONG).show();
                        requestPermissionForCamera();
                    } else {
//                    Toast.makeText(getApplicationContext(), "",Toast.LENGTH_LONG).show();
                        Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                }

                case REQUEST_FOR_CAMERA: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
//                    Toast.makeText(getApplicationContext(), "SMS Permission granted", Toast.LENGTH_LONG).show();
                        if (isVideo) {
                            videoIntent();
                        } else {
                            cameraIntent();

                        }
                    } else {
//                    Toast.makeText(getApplicationContext(), "",Toast.LENGTH_LONG).show();
                        Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                }


            }
        }

        @Override
        protected void onRestart() {
            super.onRestart();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
                    onCaptureImageResult(data);
                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        getImageFromCropActivity(result);
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        finish();
                    }
                } else if (requestCode == REQUEST_VIDEO) {
                    onCaptureVideoResult(data);
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Capturing cancel", Toast.LENGTH_SHORT);
                finish();
            }


        }

        /**
         * After capturing the video result it process the file and send using file listener callback
         * @param data
         */
        private void onCaptureVideoResult(Intent data) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String decodableString = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(decodableString);
            if (file != null) {
                fileListener.onGetFile(file);
                finish();
            } else {
                finish();
            }
        }

        private void onCaptureImageResult(Intent data) {
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                if (bitmapImage == null) {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                }
                if (!wantToCrop) {
                    listener.onGetBitmap(bitmapImage, mCurrentPhotoPath);
                    finish();
                } else {
                    showImageCropperActivity(isOval);
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }


        }

        private void showImageCropperActivity(boolean isOval) {

            CropImage.activity(file)
                    .setCropShape(isOval ? CropImageView.CropShape.OVAL : CropImageView.CropShape.RECTANGLE)
                    .setActivityMenuIconColor(getResources().getColor(android.R.color.white))
                    .setBorderCornerColor(ResourceUtil.fetchAccentColor(this))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        private void getImageFromCropActivity(CropImage.ActivityResult result) {
            if (result != null) {
                try {
                    Uri selectedImage = result.getUri();
                    bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    String imagePath = storeImage(bitmapImage);
                    listener.onGetBitmap(bitmapImage, imagePath);
                    this.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    this.finish();
                }
            } else {
                finish();
            }

        }

        private String storeImage(Bitmap image) {
            File pictureFile = getOutputMediaFile();
            String absolutePath = "";
            if (pictureFile == null) {
                //Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
                return "";
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                absolutePath = pictureFile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                // Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                // Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            return absolutePath;
        }

        private  File getOutputMediaFile(){
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            File mediaStorageDir = getCacheDir();
//            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                    + "/Android/data/"
//                    + getApplicationContext().getPackageName()
//                    + "/cache");

            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    return null;
                }
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            String mImageName="P_"+ timeStamp +".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            return mediaFile;
        }

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onStop() {
            super.onStop();
        }
    }


    public interface GetBitmapListener {
        void onGetBitmap(Bitmap bitmapImage, String filepath);
    }

    public interface GetFileListener {
        void onGetFile(File file);
    }


}
