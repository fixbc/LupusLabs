package edu.bme3890.LupusLabs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void launchWebsite(View v){

        String url = "https://www.lupus.org/";

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }


    }

    public void openInstructionsActivity(View v) {
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }

    public void openNewTestActivity(View v) {
        Intent intent = new Intent(this, NewTestActivity.class);
        startActivity(intent);
    }

    public void openMainActivity(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openOldTestsActivity(View v) {
        Intent intent = new Intent(this, OldTestsActivity.class);
        startActivity(intent);
    }

    public void openRedcapActivity(View v) {
        Intent intent = new Intent(this, RedcapActivity.class);
        startActivity(intent);
    }
}
