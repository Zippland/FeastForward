package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExpiredFoodAlert extends AppCompatActivity {
    private int userId;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Ensure food_data.csv is always available
        if (!doesFoodDataFileExist()) {
            try {
                copyRawResourceToFileIfNotExists(R.raw.food_data, "food_data.csv");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error copying food data.", Toast.LENGTH_LONG).show();
            }
        }

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tableLayout = findViewById(R.id.tblFoodExpiry);
        TextView tvHelloUser = findViewById(R.id.tvHelloUser);

        // Get user details from Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        String username = getIntent().getStringExtra("USER_NAME");

        // Display user greeting
        if (userId != -1) {
            tvHelloUser.setText("Hello User " + userId + ": " + username);
            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_LONG).show();
        } else {
            tvHelloUser.setText("Hello No User");
            Toast.makeText(this, "No valid user ID passed.", Toast.LENGTH_LONG).show();
        }

        // Populate table and check for near-expiry items
        try {
            populateTableWithUserData();
            checkNearExpiryItems();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading food data.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the Up button
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateTableWithUserData() throws IOException {
        File foodDataFile = new File(getFilesDir(), "food_data.csv");
        BufferedReader reader = new BufferedReader(new FileReader(foodDataFile));
        String line;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length >= 3) {
                String foodName = tokens[0];
                String expiryDate = tokens[1];
                String readUserId = tokens[2];
                try {
                    if (userId == Integer.parseInt(readUserId)) {
                        addRowToTable(foodName, expiryDate);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        reader.close();
    }

    private void addRowToTable(String foodName, String expiryDate) {
        TableRow row = new TableRow(this);
        row.setGravity(android.view.Gravity.CENTER);

        TextView foodTextView = new TextView(this);
        foodTextView.setText(foodName);
        foodTextView.setGravity(android.view.Gravity.CENTER);
        foodTextView.setPadding(8, 8, 8, 8);
        foodTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        TextView expiryTextView = new TextView(this);
        expiryTextView.setText(expiryDate);
        expiryTextView.setGravity(android.view.Gravity.CENTER);
        expiryTextView.setPadding(8, 8, 8, 8);
        expiryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        row.addView(foodTextView);
        row.addView(expiryTextView);

        tableLayout.addView(row);
    }


    private void checkNearExpiryItems() throws IOException {
        File foodDataFile = new File(getFilesDir(), "food_data.csv");
        BufferedReader reader = new BufferedReader(new FileReader(foodDataFile));
        String line;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        long now = new Date().getTime();
        long threeDaysInMs = 3 * 24 * 3600 * 1000;  // 3 days in milliseconds

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length >= 3) {
                String foodName = tokens[0];
                String readUserId = tokens[2];

                try {
                    if (userId == Integer.parseInt(readUserId)) {
                        Date expiryDate = sdf.parse(tokens[1]);
                        if (expiryDate != null && (expiryDate.getTime() - now) <= threeDaysInMs) {
                            showExpiryAlert(foodName, sdf.format(expiryDate));
                        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Food Details");
        builder.setMessage("Showing details for: " + foodName);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteFoodItem(foodName);
                    Toast.makeText(ExpiredFoodAlert.this, "Food item deleted successfully.", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(ExpiredFoodAlert.this, "Error deleting food item.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteFoodItem(String foodName) throws IOException {
        File foodDataFile = new File(getFilesDir(), "food_data.csv");
        BufferedReader reader = new BufferedReader(new FileReader(foodDataFile));

        ArrayList<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length >= 3 && !tokens[0].equals(foodName)) {
                lines.add(line);
            }
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(foodDataFile));
        for (String remainingLine : lines) {
            writer.write(remainingLine);
            writer.newLine();
        }
        writer.close();
    }

    private boolean doesFoodDataFileExist() {
        File foodDataFile = new File(getFilesDir(), "food_data.csv");
        return foodDataFile.exists();
    }

    private void copyRawResourceToFileIfNotExists(int resourceId, String outputFileName) throws IOException {
        InputStream inputStream = getResources().openRawResource(resourceId);
        File outputFile = new File(getFilesDir(), outputFileName);
        if (!outputFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
        }
        inputStream.close();
    }
}
