package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (checkCredentials(username, password)) {
                Toast.makeText(MainActivity.this, "Log in successfully!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainActivity.this, "The username or password is incorrect!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkCredentials(String username, String password) {

        return (username.equals("comp2100@anu.edu.au") && password.equals("comp2100")) ||
                (username.equals("comp6442@anu.edu.au") && password.equals("comp6442"));
    }
}
