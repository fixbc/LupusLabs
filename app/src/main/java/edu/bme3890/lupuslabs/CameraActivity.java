package edu.bme3890.lupuslabs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity {

    //variables to manage errors
    public static final String TAG = "TestCameraApp";

    //variables to manage camera, video and visualization
    private Camera c;

    public static final int MEDIA_TYPE_IMAGE = 1;

    CameraFileActivity mPreview;
    ImageView lastPhotoImageView;
    ImageView lastPhotoImageView2;
    ImageView lastPhotoImageView3;
    TextView notifyTextView;
    TextView timerTextView;
    TextView instructionsTextView;
    int photoNumber;
    File PictureFile1;
    File PictureFile2;
    File PictureFile3;

    //variables to manage permissions
    private Activity thisActivity;
    public static final int REQUEST_CAMERA = 29181;

    public void openGraphActivity(View v) {
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra("image1", PictureFile1.getName());
        intent.putExtra("image2", PictureFile2.getName());
        intent.putExtra("image3", PictureFile3.getName());
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //Get references to View objects ----
        Button captureButton = findViewById(R.id.captureButton);
        FrameLayout preview = findViewById(R.id.previewFrameLayout);

        lastPhotoImageView = findViewById(R.id.photoImageView);
        lastPhotoImageView2 = findViewById(R.id.photoImageView2);
        lastPhotoImageView3 = findViewById(R.id.photoImageView3);
        notifyTextView = findViewById(R.id.notifyTextView);
        timerTextView = findViewById(R.id.timerTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);

        notifyTextView.setVisibility(View.GONE);
        timerTextView.setVisibility(View.GONE);
        photoNumber = 1;

        lastPhotoImageView.setVisibility(View.GONE); // hide upon first use
        lastPhotoImageView2.setVisibility(View.GONE);
        lastPhotoImageView3.setVisibility(View.GONE);

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
        c.setDisplayOrientation(90);
        mPreview = new CameraFileActivity(this, c);
        preview.addView(mPreview);
        TextView textViewLeft = findViewById(R.id.textViewLeft);
        TextView textViewRight = findViewById(R.id.textViewRight);

        ((ViewGroup)textViewLeft.getParent()).removeView(textViewLeft);
        preview.addView(textViewLeft);

        ((ViewGroup)textViewRight.getParent()).removeView(textViewRight);
        preview.addView(textViewRight);

        //Setup Listeners for Capture ----
        // --- done through the Camera.PictureCallback method below

        //Capture and Save Files ----
        // --- done through the takePicture method implemented below

        //Release the Camera ----
        // --- done through the onPause method of the activity

    }

    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
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
            photoNumber++;
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
            camera.setDisplayOrientation(90);
            camera.startPreview();


            //make image visible within the imageView
            if (!lastPhotoImageView.isShown()) {
                lastPhotoImageView.setVisibility(View.VISIBLE);

            }
            if (pictureFile.getName().equalsIgnoreCase("image1.jpg")){
                PictureFile1 = pictureFile;
                lastPhotoImageView.setImageURI(Uri.fromFile(pictureFile));
                instructionsTextView.setVisibility(View.GONE);
                notifyTextView.setVisibility(View.VISIBLE);
                timerTextView.setVisibility(View.VISIBLE);
                notifyTextView.setText("Last picture taken: ");
                notifyTextView.append(pictureFile.getName());
            } else if (pictureFile.getName().equalsIgnoreCase("image2.jpg")) {
                PictureFile2 = pictureFile;
                lastPhotoImageView2.setVisibility(View.VISIBLE);
                lastPhotoImageView2.setImageURI(Uri.fromFile(pictureFile));
                instructionsTextView.setVisibility(View.GONE);
                notifyTextView.setVisibility(View.VISIBLE);
                timerTextView.setVisibility(View.VISIBLE);
                notifyTextView.setText("Last picture taken: ");
                notifyTextView.append(pictureFile.getName());
            } else if (pictureFile.getName().equalsIgnoreCase("image3.jpg")) {
                PictureFile3 = pictureFile;
                lastPhotoImageView3.setVisibility(View.VISIBLE);
                lastPhotoImageView3.setImageURI(Uri.fromFile(pictureFile));
                instructionsTextView.setVisibility(View.GONE);
                notifyTextView.setVisibility(View.VISIBLE);
                timerTextView.setVisibility(View.VISIBLE);
                notifyTextView.setText("Last picture taken: ");
                notifyTextView.append(pictureFile.getName());
            } else {
                lastPhotoImageView.setImageURI(Uri.fromFile(pictureFile));
                instructionsTextView.setVisibility(View.GONE);
                notifyTextView.setVisibility(View.VISIBLE);
                timerTextView.setVisibility(View.VISIBLE);
                notifyTextView.setText("Last picture taken: ");
                notifyTextView.append(pictureFile.getName());
            }

        }
    };

    public void takePicture(View v){
        notifyTextView.setText("Capturing Image...");
        c.takePicture(null, null, mPicture);

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                notifyTextView.setText("Capturing Image...");
                c.takePicture(null, null, mPicture);

                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        notifyTextView.setText("Capturing Image...");
                        c.takePicture(null, null, mPicture);
                    }
                }, 30000);
            }
        }, 30000);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time Remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerTextView.setText("Scan Complete!");
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();    // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (c != null){
            c.release();    // release the camera for other applications
            c = null;
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
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "image" + photoNumber + ".jpg");
        } else {
            return null;
        }

        return mediaFile;

    }
}
