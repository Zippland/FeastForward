package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.TextView;


import com.example.myapplication.Utils.DataUtil;

import java.util.Calendar;

public class DataActivity extends AppCompatActivity {

    private static final String TAG = "DataActivity";
    private EditText recipeInput;
    private EditText expiredInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        DataUtil.init(this);
        recipeInput = findViewById(R.id.recipe_input);
        TextView expiredDisplay = findViewById(R.id.expired_display);

        Button expiredButton = findViewById(R.id.expired_button);
        expiredButton.setOnClickListener(v -> showDatePickerDialog(expiredDisplay));

        //add data
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            String recipeName = recipeInput.getText().toString();
            String expiredTime = expiredDisplay.getText().toString();
            if (!recipeName.isEmpty() && !expiredTime.equals("Select Date")) {
                addRecipe(recipeName, expiredTime, 1);
                //clear input
                recipeInput.setText("");
                expiredDisplay.setText("Select Date");
                showContextMenu("Data added successfully！");
            } else {
                showContextMenu("Mandatory fields cannot be blank");
            }
        });

        //Read all data and display
        Button displayButton = findViewById(R.id.display_button);
        displayButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodShowActivity.class);
            startActivity(intent);
        });

        Button searchButton = findViewById(R.id.button);
        //search
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodSearchActivity.class);
            startActivity(intent);
        });
    }

    //add data
    private void addRecipe(String recipeName, String expireDate, int userId) {
        String entry = recipeName + "," + expireDate + "," + userId;
        DataUtil.writeEntryToDataset(entry);
    }

    //Show success popup
    private void showContextMenu(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("status panel")
                .setNegativeButton("OK", (dialog, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * calendar view
     *
     * @author Zihan Jian u7174903
     */
    private void showDatePickerDialog(final TextView display) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // format the display of date with leading zeros for month and day
                    String formattedDate = String.format("%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    display.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }
}
