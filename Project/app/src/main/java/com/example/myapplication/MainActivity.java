package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.loginButton);
        final TextView forgotPassword = findViewById(R.id.forgotPassword);
        final Button signUpButton = findViewById(R.id.signUpButton);
        ImageButton facebookLoginButton = findViewById(R.id.facebookLoginButton);
        ImageButton googleLoginButton = findViewById(R.id.googleLoginButton);
        ImageButton twitterLoginButton = findViewById(R.id.twitterLoginButton);

        // Set up listeners
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (checkCredentials(username, password)) {
                Toast.makeText(MainActivity.this, "Log in successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "The username or password is incorrect!", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPassword.setOnClickListener(v -> {
            // Handle forgot password click
            Toast.makeText(MainActivity.this, "Reset password feature not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        signUpButton.setOnClickListener(v -> {
            // Handle sign up click
            Toast.makeText(MainActivity.this, "Sign up feature not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        // Handle third-party sign up click
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign up click
                Toast.makeText(MainActivity.this, "Third-party sign up feature not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        };
        facebookLoginButton.setOnClickListener(listener);
        googleLoginButton.setOnClickListener(listener);
        twitterLoginButton.setOnClickListener(listener);
    }

    private boolean checkCredentials(String username, String password) {
        // Hardcoded credentials for demonstration
        return (username.equals("comp2100@anu.edu.au") && password.equals("comp2100")) ||
                (username.equals("comp6442@anu.edu.au") && password.equals("comp6442"));
    }
}
