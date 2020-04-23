package edu.bme3890.lupuslabs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class OldTestsActivity extends AppCompatActivity {

    ImageView firstTestResultImageView;
    File imageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_tests);

        firstTestResultImageView = findViewById(R.id.firstTestResultImageView);
        Intent bitmapIntent = getIntent();
        String FILENAME = bitmapIntent.getStringExtra("barBitmap");
        File mediaStorageDir = new File(getFilesDir(),"TCAImages");
        imageFile = new File(mediaStorageDir, FILENAME);
        firstTestResultImageView.setImageURI(Uri.fromFile(imageFile));
    }

    public void openEmailActivity(View v) {
        Intent intent = new Intent(this, EmailActivity.class);
        intent.putExtra(Activity.ACTIVITY_SERVICE, this.getClass().getSimpleName());
        startActivity(intent);
    }
}
