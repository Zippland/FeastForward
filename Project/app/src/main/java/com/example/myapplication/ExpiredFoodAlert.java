package com.example.myapplication;

import com.example.myapplication.Tree.BinarySearchTree;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
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

/**
 * ExpiredFoodAlert is an activity that displays food items, shared food items,
 * and alerts for near expiry items. It allows users to delete or share food items.
 *
 * @author Baizhen Lin
 */
public class ExpiredFoodAlert extends AppCompatActivity {
    int userId;
    String userName;
    TableLayout tableLayout;
    TableLayout sharedTableLayout;
    FoodDataManager foodDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Check if the food data file exists, if not, copy it from resources
        if (!doesFoodDataFileExist()) {
            try {
                copyRawResourceToFileIfNotExists(R.raw.food_data, "food_data.csv");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error copying food data.", Toast.LENGTH_LONG).show();
            }
        }

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI elements
        tableLayout = findViewById(R.id.tblFoodExpiry);
        sharedTableLayout = findViewById(R.id.tblSharedFood);
        TextView tvHelloUser = findViewById(R.id.tvHelloUser);

        // Get user ID and name from the intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        userName = getIntent().getStringExtra("USER_NAME");

        if (userId != -1) {
            tvHelloUser.setText("Hello User " + userId + ": " + userName);
            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_LONG).show();
        } else {
            tvHelloUser.setText("Hello No User");
            Toast.makeText(this, "No valid user ID passed.", Toast.LENGTH_LONG).show();
        }

        // Initialize FoodDataManager
        foodDataManager = new FoodDataManager(
                userId,
                new FoodDataManager.DefaultReaderProvider(),
                new FoodDataManager.DefaultWriterProvider()
        );

        // Populate tables and check for near expiry items
        try {
            populateTableWithUserData();
            populateSharedTable();
            checkNearExpiryItems();
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

    /**
     * Populates the table with food data specific to the user.
     *
     * @throws IOException if an I/O error occurs
     */
    private void populateTableWithUserData() throws IOException, ParseException {
        File foodDataFile = getFoodDataFile();
        BinarySearchTree userData = foodDataManager.getUserData(foodDataFile);

        userData.traverseInOrder(node -> addRowToTable(node.foodName, foodDataManager.sdf.format(node.expiryDate), node.isShared));
    }

    /**
     * Adds a row to the table with food data.
     *
     * @param foodName   the name of the food item
     * @param expiryDate the expiry date of the food item
     * @param isShared   the shared status of the food item
     */
    private void addRowToTable(String foodName, String expiryDate, String isShared) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);

        // Create TextView for food name
        TextView foodTextView = new TextView(this);
        foodTextView.setText(foodName);
        foodTextView.setGravity(Gravity.CENTER);
        foodTextView.setPadding(8, 8, 8, 8);
        foodTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

        // Create TextView for expiry date
        TextView expiryTextView = new TextView(this);
        expiryTextView.setText(expiryDate);
        expiryTextView.setGravity(Gravity.CENTER);
        expiryTextView.setPadding(8, 8, 8, 8);
        expiryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

        // Create Delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12);
        deleteButton.setPadding(4, 4, 4, 4);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        buttonParams.setMargins(4, 4, 2, 4);
        deleteButton.setLayoutParams(buttonParams);

        // Set onClickListener for Delete button
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

        // Create Share button
        Button shareButton = new Button(this);
        shareButton.setText("Share");
        shareButton.setTextSize(12);
        shareButton.setPadding(4, 4, 4, 4);
        TableRow.LayoutParams shareButtonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        shareButtonParams.setMargins(2, 4, 4, 4);
        shareButton.setLayoutParams(shareButtonParams);

        // Disable Share button if already shared
        if ("yes".equalsIgnoreCase(isShared)) {
            shareButton.setEnabled(false);
        } else {
            shareButton.setOnClickListener(v -> shareFoodItem(foodName, shareButton));
        }

        // Add views to the row
        row.addView(foodTextView);
        row.addView(expiryTextView);
        row.addView(deleteButton);
        row.addView(shareButton);

        // Add row to the table
        tableLayout.addView(row);
    }

    /**
     * Shares a food item with others by updating its shared status.
     *
     * @param foodName   the name of the food item
     * @param shareButton the button that was clicked
     */
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

    /**
     * Checks for food items that are near expiry and shows an alert for each.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if the date format is invalid
     */
    private void checkNearExpiryItems() throws IOException, ParseException {
        File foodDataFile = getFoodDataFile();
        List<String[]> nearExpiryItems = foodDataManager.getNearExpiryItems(foodDataFile);

        for (String[] item : nearExpiryItems) {
            showExpiryAlert(item[0], item[1]);
        }
    }

    /**
     * Shows an alert for a food item that is near expiry.
     *
     * @param foodName   the name of the food item
     * @param expiryDate the expiry date of the food item
     */
    private void showExpiryAlert(String foodName, String expiryDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Expiration Alert");
        builder.setMessage("The food item " + foodName + " is near expiration (expires on: " + expiryDate + ").");
        builder.setNegativeButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Checks if the food data file exists.
     *
     * @return true if the file exists, false otherwise
     */
    private boolean doesFoodDataFileExist() {
        File foodDataFile = new File(getFilesDir(), "food_data.csv");
        return foodDataFile.exists();
    }

    /**
     * Copies a raw resource file to the application's file directory if it does not already exist.
     *
     * @param resourceId the resource ID of the raw file
     * @param outputFileName the name of the output file
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Populates the table with shared food data.
     *
     * @throws IOException if an I/O error occurs
     */
    private void populateSharedTable() throws IOException {
        File foodDataFile = getFoodDataFile();
        List<String[]> sharedData = foodDataManager.getSharedData(foodDataFile);

        for (String[] data : sharedData) {
            addSharedRowToTable(data[4], data[0], data[1]);
        }
    }

    /**
     * Adds a row to the shared food table.
     *
     * @param userName   the name of the user who shared the food item
     * @param foodName   the name of the food item
     * @param expiryDate the expiry date of the food item
     */
    private void addSharedRowToTable(String userName, String foodName, String expiryDate) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER);

        // Create TextView for user name
        TextView userTextView = new TextView(this);
        userTextView.setText(userName);
        userTextView.setGravity(Gravity.CENTER);
        userTextView.setPadding(8, 8, 8, 8);
        userTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        // Create TextView for food name
        TextView foodTextView = new TextView(this);
        foodTextView.setText(foodName);
        foodTextView.setGravity(Gravity.CENTER);
        foodTextView.setPadding(8, 8, 8, 8);
        foodTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        // Create TextView for expiry date
        TextView expiryTextView = new TextView(this);
        expiryTextView.setText(expiryDate);
        expiryTextView.setGravity(Gravity.CENTER);
        expiryTextView.setPadding(8, 8, 8, 8);
        expiryTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        // Add views to the row
        row.addView(userTextView);
        row.addView(foodTextView);
        row.addView(expiryTextView);

        // Add row to the shared table
        sharedTableLayout.addView(row);
    }

    /**
     * Gets the food data file from the application's file directory.
     *
     * @return the food data file
     */
    protected File getFoodDataFile() {
        return new File(getFilesDir(), "food_data.csv");
    }
}
