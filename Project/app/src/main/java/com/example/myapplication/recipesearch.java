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
        String ingredientLowerCase = ingredient.toLowerCase();
        for (String recipe : recipes) {
            // Calculate the similarity between the recipe name and the target ingredient
            double similarity = calculateSimilarity(recipe.toLowerCase(), ingredientLowerCase);
            // If similarity reaches threshold, add recipe to results
            if (similarity >= 0.6) { // can change
                filteredRecipes.add(recipe);
            }
        }
        return filteredRecipes;
    }

    private double calculateSimilarity(String s1, String s2) {
        int editDistance = editDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        return 1.0 - ((double) editDistance / maxLength);
    }

    private int editDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[m][n];
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