package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class recipesearch extends AppCompatActivity {
    private ArrayList<String> recipes = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipesearch);
        // Load the recipes data from the JSON file
        // json url: https://github.com/sami9644/Food-recipes-json-file/blob/main/recipes.json


        try {
            String json = loadJSONFromAsset("recipe1.json");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                JSONArray ingredientsArray = jsonObject.getJSONArray("Ingredients");
                String ingredients = ingredientsArray.join(", ");
                JSONArray methodArray = jsonObject.getJSONArray("Method");
                String method = methodArray.join(". ");
                String recipe = name + ": \n\nIngredients - " + ingredients + ". \n\nDirections - " + method + ".";
                recipes.add(recipe);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load the recipes data from the csv file
        // csv url: https://www.kaggle.com/datasets/thedevastator/better-recipes-for-a-better-life
        try {
            InputStream is = getAssets().open("recipe2 modi.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            reader.readLine(); // Skip the header line
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\",\""); // Split by quotes and commas
                if (data.length >= 3) { // Check if all needed data is present
                    String name = data[0].replaceAll("^\"|\"$", ""); // Remove the enclosing double quotes
                    String ingredients = data[1].replaceAll("^\"|\"$", ""); // Remove the enclosing double quotes
                    String method = data[2].replaceAll("^\"|\"$", ""); // Remove the enclosing double quotes
                    String recipe = name + ": \n\nIngredients - " + ingredients + ". \n\nDirections - " + method + ".";
                    recipes.add(recipe);
                }
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditText ingredientEditText = findViewById(R.id.ingredientEditText);
        Button button = findViewById(R.id.button);
        TextView recipeTextView = findViewById(R.id.recipeTextView);

        // Set a click listener for the Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered ingredient
                String ingredient = ingredientEditText.getText().toString();

                // Get the recipes with the entered ingredient
                List<String> filteredRecipes = getRecipesWithIngredient(ingredient);

                // Display the recipes in the TextView
                if (!filteredRecipes.isEmpty()) {
                    //recipeTextView.setText(filteredRecipes.get(0)); // can get random recipe below
                    Random random = new Random();
                    int randomIndex = random.nextInt(filteredRecipes.size());
                    recipeTextView.setText(filteredRecipes.get(randomIndex));

                } else {
                    recipeTextView.setText("No recipes found with the entered ingredient.");
                }
            }
        });

    }



    private List<String> getRecipesWithIngredient(String ingredient) {
        List<String> filteredRecipes = new ArrayList<>();
        for (String recipe : recipes) {
            if (recipe.toLowerCase().contains(ingredient.toLowerCase())) {
                filteredRecipes.add(recipe);
            }
        }
        return filteredRecipes;
    }

    // Method to load JSON from asset
    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}