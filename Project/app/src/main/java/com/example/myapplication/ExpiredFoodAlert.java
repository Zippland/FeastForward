package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ExpiredFoodAlert extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("success");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView foodName = findViewById(R.id.foodName);
        int userId = getIntent().getIntExtra("USER_ID", -1);
        String username = getIntent().getStringExtra("USER_NAME");
        if (userId != -1) {
            foodName.setText("Hello User " + userId +":" + username);
            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_LONG).show();//can be removed
        } else {
            foodName.setText("Hello No User ");
            Toast.makeText(this, "No valid user ID passed.", Toast.LENGTH_LONG).show();
        }



        try {
            checkNearExpiryItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkNearExpiryItems() throws IOException {
        // Open the CSV file from assets
        Toast.makeText(ExpiredFoodAlert.this, "Hi", Toast.LENGTH_SHORT).show();
        InputStream is = getResources().openRawResource(R.raw.food_data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        long now = new Date().getTime();
        long threeDaysInMs = 3 * 24 * 3600 * 1000;  // 3 days in milliseconds

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length >= 2) {
                String foodName = tokens[0];
                try {
                    Date expiryDate = sdf.parse(tokens[1]);
                    if (expiryDate != null && (expiryDate.getTime() - now) <= threeDaysInMs) {
                        showExpiryAlert(foodName, sdf.format(expiryDate));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        reader.close();
    }

    private void showExpiryAlert(String foodName, String expiryDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Expiration Alert");
        builder.setMessage("The food item " + foodName + " is near expiration (expires on: " + expiryDate + "). Click OK to view details.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewFoodItem(foodName);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void viewFoodItem(String foodName) {
        // Here you can start a new Activity or fragment to show details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Food Details");
        builder.setMessage("Showing details for: " + foodName);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
