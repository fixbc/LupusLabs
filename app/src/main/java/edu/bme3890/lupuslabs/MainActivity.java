package edu.bme3890.lupuslabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.bme3890.lupuslabs.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;

    EditText emailEditText, passwordEditText;
    Button createAccButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        createAccButton = (Button) findViewById(R.id.createAccButton);

        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = new User(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getEmail()).exists())
                            Toast.makeText(MainActivity.this, "This email is already registered", Toast.LENGTH_SHORT).show();
                        else {
                            users.child(user.getEmail()).setValue(user);
                            Toast.makeText(MainActivity.this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //

                    }
                });
            }
        });
    }

    public void openMainMenuActivity(View v) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    public void openCreateAccountActivity(View v) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void openForgotPasswordActivity(View v) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void openAboutUsActivity(View v) {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }
}
