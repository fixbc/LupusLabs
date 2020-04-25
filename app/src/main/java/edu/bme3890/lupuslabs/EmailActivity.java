package edu.bme3890.lupuslabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class EmailActivity extends AppCompatActivity {
    private EditText mEditTextTo;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        String emailAddress = mAuth.getCurrentUser().getEmail();

        mEditTextTo = findViewById(R.id.edit_text_to);
        mEditTextTo.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEditTextSubject = findViewById(R.id.edit_text_subject);
        mEditTextMessage = findViewById(R.id.edit_text_message);

        Button buttonsend = findViewById(R.id.button_send);
        buttonsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        Intent dataIntent = getIntent();
        String sensor_data = dataIntent.getStringExtra(NewTestActivity.EXTRA_SENSOR_DATA);
        String startActivity = dataIntent.getStringExtra(Activity.ACTIVITY_SERVICE);
        String results = dataIntent.getStringExtra("results");
        String newTestActivity = NewTestActivity.class.getSimpleName();
        String graphActivity = GraphActivity.class.getSimpleName();

        if (startActivity.equalsIgnoreCase(newTestActivity)) {
            mEditTextMessage.setText("Team name: Lupus Labs\n\nLinear acceleration:\n" + sensor_data);
        } else if (startActivity.equalsIgnoreCase(graphActivity)) {
            mEditTextTo.setText(emailAddress);
            mEditTextMessage.setText("Results for " + emailAddress + ":\n" + results);

        }
    }

    private void sendMail() {
        String recipientList = mEditTextTo.getText().toString();
        String[] recipients = recipientList.split( ",");

        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}
