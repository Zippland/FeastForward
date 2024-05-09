package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.FileHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.os.Bundle;
import android.widget.EditText;
import java.util.Calendar;

public class DataActivity extends AppCompatActivity {

    private EditText recipeInput, expiredInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        recipeInput = findViewById(R.id.recipe_input);
        expiredInput = findViewById(R.id.expired_input);
        expiredInput.setOnClickListener(v -> showDatePickerDialog());

        //添加数据
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            View rootView = getWindow().getDecorView().getRootView();
            String recipeName = recipeInput.getText().toString();
            String expiredTime = expiredInput.getText().toString();
            if (!recipeName.isEmpty() && !expiredTime.isEmpty()) {
                addRecipe(recipeName, expiredTime, 1);
                //clear input
                recipeInput.setText("");
                expiredInput.setText("");
                showContextMenu("Data added successfully！");
            } else {
                showContextMenu("Mandatory fields cannot be blank");
            }
        });

        //Read all data and display
        Button displayButton = findViewById(R.id.display_button);
        displayButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipesShowActivity.class);
            startActivity(intent);
        });

        Button searchButton = findViewById(R.id.button);
        //search
        searchButton.setOnClickListener(v -> {

        });
    }

    //add data
    private void addRecipe(String recipeName, String expireDate, int userId) {
        String entry = recipeName + "," + expireDate + "," + userId;
        writeEntryToDataset(entry);
    }

    //write data to dataset
    private void writeEntryToDataset(String entry) {
        File file = new File(getFilesDir(), "dataset.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(entry);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> expiredInput.setText(String.format("%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth)),
                year, month, day);
        datePickerDialog.show();
    }

}
