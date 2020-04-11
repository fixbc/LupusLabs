package edu.bme3890.lupuslabs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.VideoSource;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.bme3890.lupuslabs.CameraFileActivity;
import edu.bme3890.lupuslabs.R;

public class CameraActivity extends AppCompatActivity {

    //variables to manage errors
    public static final String TAG = "TestCameraApp";

    //variables to manage camera, video and visualization
    private Camera c;
    private MediaRecorder videoMR = new MediaRecorder();
    private boolean isRecording = false;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    CameraFileActivity mPreview;
    ImageView lastPhotoImageView;
    TextView notifyTextView;

    //variables to manage permissions
    private Activity thisActivity;
    public static final int REQUEST_CAMERA = 29181;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //Get references to View objects ----
        Button captureButton = (Button) findViewById(R.id.captureButton);
        Button videoButton = (Button) findViewById(R.id.videoButton);
        FrameLayout preview = (FrameLayout) findViewById(R.id.previewFrameLayout);

        lastPhotoImageView = (ImageView) findViewById(R.id.photoImageView);
        notifyTextView = (TextView) findViewById(R.id.notifyTextView);

        lastPhotoImageView.setVisibility(View.GONE); // hide upon first use

        //Detect and Access Camera ----
        //check if camera exists - may not be needed if camera is required by app
        checkCameraHardware(this);

        //get access to the camera
        thisActivity = this;
        checkCameraPermission(this, thisActivity);
        c = getCameraInstance();

        //Create a Preview Class ----
        // --- done as a new java class named "CameraFileActivity.java"

        //Build a Preview Layout ----
        // --- done as part of the activity_main layout file

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraFileActivity(this, c);
        preview.addView(mPreview);

        //Setup Listeners for Capture ----
        // --- done through the Camera.PictureCallback method below

        //Capture and Save Files ----
        // --- done through the takePicture method implemented below

        //Release the Camera ----
        // --- done through the onPause method of the activity

        //disable video button temporarily
        videoButton.setEnabled(false);

    }


    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private boolean checkCameraPermission(Context context, Activity activity) {
        boolean hadPermission; // returns whether system HAD permission or not at install

        if (Build.VERSION.SDK_INT >= 23) { //only run for SDK 23 and above

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                hadPermission = false;
                //request permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            } else {
                hadPermission = true; //
            }
            return hadPermission;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            //make preview available again
            camera.startPreview();

            //make image visible within the imageView
            if (!lastPhotoImageView.isShown()) {
                lastPhotoImageView.setVisibility(View.VISIBLE);
            }
            lastPhotoImageView.setImageURI(Uri.fromFile(pictureFile));
            notifyTextView.setText("Last picture taken:\n");
            notifyTextView.append(pictureFile.getName());
        }
    };

    public void takePicture(View v){
        notifyTextView.setText("Capturing Image...");
        c.takePicture(null, null, mPicture);
    }

    private boolean prepareVideoRecorder(){
        releaseCamera();
        c = getCameraInstance();
        videoMR = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        c.unlock();
        videoMR.setCamera(c);

        // Step 2: Set sources
        // videoMR.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        videoMR.setVideoSource(VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        videoMR.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        videoMR.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        videoMR.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            videoMR.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /*public void takeVideo(View v){
        if (isRecording) { //button press toggles recording state
            notifyTextView.setText("Done recoriding video!");
            videoMR.stop();
            videoMR.release();;
            c.lock();
            isRecording = false;
        } else {
            notifyTextView.setText("Recording Video...");
            lastPhotoImageView.setVisibility(View.GONE);
            isRecording = true;

            //?? disable capture button??//

            // Step 1: Unlock and set camera to MediaRecorder
            c.unlock();
            videoMR.setCamera(c);

            // Step 2: Set sources
            //videoMR.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            videoMR.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

            // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
            videoMR.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

            // Step 4: Set output file
            videoMR.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

            // Step 5: Set the preview output
            videoMR.setPreviewDisplay(mPreview.getHolder().getSurface());

            // Step 6: Prepare configured MediaRecorder
            try {
                videoMR.prepare();
            } catch (IllegalStateException e) {
                Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
                releaseMediaRecorder();
            } catch (IOException e) {
                Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
                releaseMediaRecorder();
            }

            videoMR.start();
        }
    }*/


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder(); // release media recorder first if in use
        releaseCamera();    // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (c != null){
            c.release();    // release the camera for other applications
            c = null;
        }
    }

    private void releaseMediaRecorder(){
        if (videoMR != null) {
            videoMR.reset();   // clear recorder configuration
            videoMR.release(); // release the recorder object
            videoMR = null;
            c.lock();           // lock camera for later use
        }
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){

        //create an internal directory to store photos
        File mediaStorageDir = new File(getFilesDir(),"TCAImages");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("TCAImages", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
