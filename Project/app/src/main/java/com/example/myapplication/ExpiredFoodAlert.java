package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class ExpiredFoodAlert extends AppCompatActivity {
    int userId;
    String userName;
    TableLayout tableLayout;
    TableLayout sharedTableLayout;

    public FoodDataManager foodDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (!doesFoodDataFileExist()) {
            try {
                copyRawResourceToFileIfNotExists(R.raw.food_data, "food_data.csv");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error copying food data.", Toast.LENGTH_LONG).show();
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tableLayout = findViewById(R.id.tblFoodExpiry);
        sharedTableLayout = findViewById(R.id.tblSharedFood);
        TextView tvHelloUser = findViewById(R.id.tvHelloUser);

        userId = getIntent().getIntExtra("USER_ID", -1);
        userName = getIntent().getStringExtra("USER_NAME");

        System.out.println("1");

        if (userId != -1) {
            tvHelloUser.setText("Hello User " + userId + ": " + userName);
            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_LONG).show();
        } else {
            tvHelloUser.setText("Hello No User");
            Toast.makeText(this, "No valid user ID passed.", Toast.LENGTH_LONG).show();
        }

        System.out.println("2");
        foodDataManager = new FoodDataManager(
                userId,
                new FoodDataManager.DefaultReaderProvider(),
                new FoodDataManager.DefaultWriterProvider()
        );

        System.out.println("3");
        try {
            System.out.println("4");
            populateTableWithUserData();
            System.out.println("5");
            populateSharedTable();
            System.out.println("6");
            checkNearExpiryItems();
            System.out.println("7");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading food data.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void populateTableWithUserData() throws IOException {
        System.out.println("4.0");
        File foodDataFile = getFoodDataFile();
        System.out.println("4.05");
        List<String[]> userData = foodDataManager.getUserData(foodDataFile);
        System.out.println("4.1: User data size: " + userData.size());
        for (String[] data : userData) {
            System.out.println("4.2: Adding row with data " + String.join(", ", data));
            addRowToTable(data[0], data[1], data[3]);
        }
        System.out.println("4.5");
    }

    private void addRowToTable(String foodName, String expiryDate, String isShared) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);
        System.out.println("4.2");
        TextView foodTextView = new TextView(this);
        foodTextView.setText(foodName);
        foodTextView.setGravity(Gravity.CENTER);
        foodTextView.setPadding(8, 8, 8, 8);
        foodTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

        TextView expiryTextView = new TextView(this);
        expiryTextView.setText(expiryDate);
        expiryTextView.setGravity(Gravity.CENTER);
        expiryTextView.setPadding(8, 8, 8, 8);
        expiryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12);
        deleteButton.setPadding(4, 4, 4, 4);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        buttonParams.setMargins(4, 4, 2, 4);
        deleteButton.setLayoutParams(buttonParams);

        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete the item: " + foodName + "?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                try {
                    foodDataManager.deleteFoodItem(getFoodDataFile(), foodName);
                    tableLayout.removeView(row);
                    Toast.makeText(ExpiredFoodAlert.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ExpiredFoodAlert.this, "Error deleting the item.", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        Button shareButton = new Button(this);
        shareButton.setText("Share");
        shareButton.setTextSize(12);
        shareButton.setPadding(4, 4, 4, 4);
        TableRow.LayoutParams shareButtonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        shareButtonParams.setMargins(2, 4, 4, 4);
        shareButton.setLayoutParams(shareButtonParams);
        if ("yes".equalsIgnoreCase(isShared)) {
            shareButton.setEnabled(false);
        } else {
            shareButton.setOnClickListener(v -> shareFoodItem(foodName, shareButton));
        }

        row.addView(foodTextView);
        row.addView(expiryTextView);
        row.addView(deleteButton);
        row.addView(shareButton);

        tableLayout.addView(row);
    }

    private void shareFoodItem(String foodName, Button shareButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share Food");
        builder.setMessage("Are you sure you want to share the food with others?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            try {
                foodDataManager.updateFoodSharedStatus(getFoodDataFile(), foodName);
                shareButton.setEnabled(false);
                Toast.makeText(ExpiredFoodAlert.this, "Food shared successfully.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(ExpiredFoodAlert.this, "Error updating food share status.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkNearExpiryItems() throws IOException, ParseException {
        File foodDataFile = getFoodDataFile();
        List<String[]> nearExpiryItems = foodDataManager.getNearExpiryItems(foodDataFile);

        for (String[] item : nearExpiryItems) {
            showExpiryAlert(item[0], item[1]);
        }
    }

    private void showExpiryAlert(String foodName, String expiryDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Expiration Alert");
        builder.setMessage("The food item " + foodName + " is near expiration (expires on: " + expiryDate + ").");
        builder.setNegativeButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
        File foodDataFile = getFoodDataFile();
        List<String[]> sharedData = foodDataManager.getSharedData(foodDataFile);

        for (String[] data : sharedData) {
            addSharedRowToTable(data[4], data[0], data[1]);
        }
    }

    private void addSharedRowToTable(String userName, String foodName, String expiryDate) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER);

        TextView userTextView = new TextView(this);
        userTextView.setText(userName);
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

    protected File getFoodDataFile() {
        return new File(getFilesDir(), "food_data.csv");
    }
}
