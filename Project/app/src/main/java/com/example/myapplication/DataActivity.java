package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.Recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {

    private static final String TAG = "DataActivity";
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        recipes = new ArrayList<>();
        loadRecipesFromCSV(); // Load Dataset

        // Display of loaded data
        for (Recipe recipe : recipes) {
            Log.d(TAG, recipe.toString());
        }

        Button modifyButton = findViewById(R.id.modify_button);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!recipes.isEmpty()) {
                    Recipe firstRecipe = recipes.get(0);
                    firstRecipe.setTitle("Modified Title");
                    Log.d(TAG, "Modified Recipe: " + firstRecipe.toString());
                }
            }
        });
    }

    private void loadRecipesFromCSV() {
        InputStream inputStream = getResources().openRawResource(R.raw.recipes); // The dataset files are in the res/raw folder.
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                Recipe recipe = new Recipe(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                recipes.add(recipe);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
