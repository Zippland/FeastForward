package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.FileHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataActivity extends AppCompatActivity {

    private static final String TAG = "DataActivity";
    private EditText recipeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        recipeInput = findViewById(R.id.recipe_input);

        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipeName = recipeInput.getText().toString();
                if (!recipeName.isEmpty()) {
                    addRecipe(recipeName, "2024-04-30", 1); // example
                    recipeInput.setText(""); // Clear the input field after adding
                }
            }
        });

        Button displayButton = findViewById(R.id.display_button);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRecipesForUser(1); // user number
            }
        });

        Button searchButton = findViewById(R.id.button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform search action
            }
        });
    }

    private void addRecipe(String recipeName, String expireDate, int userId) {
        String entry = recipeName + "," + expireDate + "," + userId;
        writeEntryToDataset(entry);
    }

    private void writeEntryToDataset(String entry) {
        File file = new File(getFilesDir(), "dataset.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayRecipesForUser(int userId) {
        File file = new File(getFilesDir(), "dataset.csv");
        if (!file.exists()) {
            Log.d(TAG, "Dataset file not found");
            return;
        }

        try {
            // read from dataset
            String[] lines = FileHelper.readFileLines(file);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int user = Integer.parseInt(parts[2].trim());
                    if (user == userId) {
                        String recipeName = parts[0].trim();
                        String expireDate = parts[1].trim();
                        Log.d(TAG, "Recipe for user " + userId + ": " + recipeName + ", " + expireDate);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
