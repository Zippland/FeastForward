package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

/**
 * Login page
 *
 * @author Zihan Jian u7174903
 */
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

        // Clear internal storage files
        cleanInternalStorage();

        // Set up listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                int userId = getUserId(username);
                String usernickname = getUserNickName(username);
                if (checkCredentials(username, password)) {
                    Toast.makeText(MainActivity.this, "Log in successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, AfterLogin.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("USER_NAME", username);
                    intent.putExtra("USER_NICKNAME", usernickname);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "The username or password is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password click
                Toast.makeText(MainActivity.this, "Reset password feature not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign up click
                Toast.makeText(MainActivity.this, "Sign up feature not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle third-party sign up click
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder for third-party login functionality
                Toast.makeText(MainActivity.this, "Third-party sign up feature not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        };
        facebookLoginButton.setOnClickListener(listener);
        googleLoginButton.setOnClickListener(listener);
        twitterLoginButton.setOnClickListener(listener);
    }

    private int getUserId(String username) {
        switch (username) {
            case "comp2100@anu.edu.au":
                return 1;
            case "comp6442@anu.edu.au":
                return 2;
            case "a":
                return 3;
            default:
                return -1; // Invalid user
        }
    }

    private String getUserNickName(String username) {
        switch (username) {
            case "comp2100@anu.edu.au":
                return "comp2100";
            case "comp6442@anu.edu.au":
                return "comp6442";
            case "a":
                return "user Aa";
            default:
                return ""; // Invalid user
        }
    }

    private boolean checkCredentials(String username, String password) {
        // Hardcoded credentials for demonstration
        return (username.equals("comp2100@anu.edu.au") && password.equals("comp2100")) ||
                (username.equals("comp6442@anu.edu.au") && password.equals("comp6442")) ||
                (username.equals("a") && password.equals("1"));
    }

    private void cleanInternalStorage() {
        File directory = getFilesDir(); // Or getCacheDir() if you want to clear cache
        if (directory != null && directory.isDirectory()) {
            String[] children = directory.list();
            if (children != null) {
                for (String child : children) {
                    new File(directory, child).delete();
                }
            }
        }
    }
}
