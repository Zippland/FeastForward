package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
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
    private String userName;
    private TableLayout tableLayout;
    private TableLayout sharedTableLayout;

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
        sharedTableLayout = findViewById(R.id.tblSharedFood); // Initialize the shared table layout
        TextView tvHelloUser = findViewById(R.id.tvHelloUser);

        // Get user details from Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        userName = getIntent().getStringExtra("USER_NAME");

        // Display user greeting
        if (userId != -1) {
            tvHelloUser.setText("Hello User " + userId + ": " + userName);
            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_LONG).show();
        } else {
            tvHelloUser.setText("Hello No User");
            Toast.makeText(this, "No valid user ID passed.", Toast.LENGTH_LONG).show();
        }

        // Populate table and check for near-expiry items
        try {
            populateTableWithUserData(); // Existing user data table
            populateSharedTable(); // New method to populate the shared food table
            checkNearExpiryItems(); // Existing expiry check
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
            if (tokens.length >= 5) {
                String foodName = tokens[0];
                String expiryDate = tokens[1];
                String readUserId = tokens[2];
                String isShared = tokens[3];
                try {
                    if (userId == Integer.parseInt(readUserId)) {
                        addRowToTable(foodName, expiryDate, isShared);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        reader.close();
    }


    private void addRowToTable(String foodName, String expiryDate, String isShared) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);

        // Text views for food name and expiry date
        TextView foodTextView = new TextView(this);
        foodTextView.setText(foodName);
        foodTextView.setGravity(Gravity.CENTER);
        foodTextView.setPadding(8, 8, 8, 8);
        foodTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2)); // Increased weight for food name

        TextView expiryTextView = new TextView(this);
        expiryTextView.setText(expiryDate);
        expiryTextView.setGravity(Gravity.CENTER);
        expiryTextView.setPadding(8, 8, 8, 8);
        expiryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2)); // Increased weight for expiry date

        // Buttons for delete and share with reduced size
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12); // Smaller text size
        deleteButton.setPadding(4, 4, 4, 4); // Reduced padding
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        buttonParams.setMargins(4, 4, 2, 4); // Adjusted margins
        deleteButton.setLayoutParams(buttonParams);
        deleteButton.setOnClickListener(v -> {
            try {
                deleteFoodItem(foodName); // Delete the food item
                tableLayout.removeView(row); // Remove the row from the table
                Toast.makeText(this, "Item deleted.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button shareButton = new Button(this);
        shareButton.setText("Share");
        shareButton.setTextSize(12); // Smaller text size
        shareButton.setPadding(4, 4, 4, 4); // Reduced padding
        TableRow.LayoutParams shareButtonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        shareButtonParams.setMargins(2, 4, 4, 4); // Adjusted margins
        shareButton.setLayoutParams(shareButtonParams);
        if ("yes".equalsIgnoreCase(isShared)) {
            shareButton.setEnabled(false); // Disable button if the item is already shared
        } else {
            shareButton.setOnClickListener(v -> {
                shareFoodItem(foodName); // Share the food item
            });
        }

        // Adding views to the row
        row.addView(foodTextView);
        row.addView(expiryTextView);
        row.addView(deleteButton);
        row.addView(shareButton);

        tableLayout.addView(row);
    }




    private void shareFoodItem(String foodName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share Food");
        builder.setMessage("Are you sure you want to share the food with others?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this food item: " + foodName);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                try {
                    updateFoodSharedStatus(foodName);
                } catch (IOException e) {
                    Toast.makeText(ExpiredFoodAlert.this, "Error updating food share status.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateFoodSharedStatus(String foodName) throws IOException {
        File foodDataFile = new File(getFilesDir(), "food_data.csv");
        BufferedReader reader = new BufferedReader(new FileReader(foodDataFile));
        ArrayList<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length >= 5 && tokens[0].equals(foodName)) {
                // Change the IsShared status to "yes"
                tokens[3] = "yes";
                line = tokens[0] + "," + tokens[1] + "," + tokens[2] + "," + tokens[3]+ "," + tokens[4];
            }
            lines.add(line);
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(foodDataFile, false)); // Overwrite the file
        for (String updatedLine : lines) {
            writer.write(updatedLine);
            writer.newLine();
        }
        writer.close();
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
            if (tokens.length >= 4) {
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
        builder.setMessage("The food item " + foodName + " is near expiration (expires on: " + expiryDate + ").");
        builder.setNegativeButton("Ok", null);
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
            if (tokens.length >= 4 && !tokens[0].equals(foodName)) {
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
    private void populateSharedTable() throws IOException {
        File foodDataFile = new File(getFilesDir(), "food_data.csv");
        BufferedReader reader = new BufferedReader(new FileReader(foodDataFile));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length >= 5 && "yes".equalsIgnoreCase(tokens[3]) && userId != Integer.parseInt(tokens[2])) {
                // Assuming token order: FoodName, ExpiryDate, UserId, IsShared
                addSharedRowToTable(tokens[4], tokens[0], tokens[1]); // sharedUser, foodName, expiryDate
            }
        }
        reader.close();
    }

    private void addSharedRowToTable(String userName, String foodName, String expiryDate) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER);

        TextView userTextView = new TextView(this);
        userTextView.setText(userName); // Ensure userName is correctly fetched
        userTextView.setGravity(Gravity.CENTER);
        userTextView.setPadding(8, 8, 8, 8);
        userTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        TextView foodTextView = new TextView(this);
        foodTextView.setText(foodName);
        foodTextView.setGravity(Gravity.CENTER);
        foodTextView.setPadding(8, 8, 8, 8);
        foodTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        TextView expiryTextView = new TextView(this);
        expiryTextView.setText(expiryDate);
        expiryTextView.setGravity(Gravity.CENTER);
        expiryTextView.setPadding(8, 8, 8, 8);
        expiryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        row.addView(userTextView);
        row.addView(foodTextView);
        row.addView(expiryTextView);

        sharedTableLayout.addView(row);
    }


}
