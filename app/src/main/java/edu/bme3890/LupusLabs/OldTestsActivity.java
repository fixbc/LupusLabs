package edu.bme3890.LupusLabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OldTestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_tests);
    }

    public void openEmailActivity(View v) {
        Intent intent = new Intent(this, EmailActivity.class);
        startActivity(intent);
    }
}
