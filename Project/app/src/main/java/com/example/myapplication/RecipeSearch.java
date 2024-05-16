/**
 * DESCRIPTION: This activity aims to search recipes with ingredients.
 * CONTRIBUTION: Most methods are created by Zhongyi Ding, except calculateSimilarity and editDistance created by Yue Yu.
 * @author Zhongyi Ding u7619253; Yue Yu u7709381
 *
 */
package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;


public class RecipeSearch extends AppCompatActivity {
    private ArrayList<String> recipes = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipesearch);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        EditText ingredientEditText = findViewById(R.id.ingredientEditText);
        Button searchButton = findViewById(R.id.button);
        TextView recipeTextView = findViewById(R.id.recipeTextView);

        // Initially disable the search button until recipes are loaded
        searchButton.setEnabled(false);
        mainThreadHandler.post(() -> {
            Toast.makeText(getApplicationContext(), "Recipes are loading, please wait...", Toast.LENGTH_LONG).show();
        });
        executor.execute(() -> {
            loadFromJson();
            loadFromCsv();

            mainThreadHandler.post(() -> {
                // Enable the button now that the data is loaded
                searchButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Recipes Loaded", Toast.LENGTH_SHORT).show();
            });
        });

        searchButton.setOnClickListener(v -> {
            String ingredient = ingredientEditText.getText().toString();
            List<String> filteredRecipes = getRecipesWithIngredient(ingredient);
            if (!filteredRecipes.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(filteredRecipes.size());
                recipeTextView.setText(filteredRecipes.get(randomIndex));
            } else {
                recipeTextView.setText("No recipes found with the entered ingredient.");
            }
        });
    }






    private List<String> getRecipesWithIngredient(String ingredient) {
        List<String> filteredRecipes = new ArrayList<>();
        String inputLowerCase = ingredient.toLowerCase().trim();

        for (String recipe : recipes) {
            int start = recipe.indexOf("Ingredients - ") + "Ingredients - ".length();
            int end = recipe.indexOf(". ", start);
            String ingredientsText = recipe.substring(start, end);
            String[] ingredients = ingredientsText.split(", ");

            for (String ingredientItem : ingredients) {
                if (calculateSimilarity(ingredientItem.trim().toLowerCase(), inputLowerCase) >= 0.5) {  // Adjust similarity threshold as needed
                    filteredRecipes.add(recipe);
                    break;  // Stop checking further ingredients once a match is found
                }
            }
        }
        return filteredRecipes;
    }
    private void loadFromJson(){
        // Load the recipes data from the JSON file
        // json url: https://github.com/sami9644/Food-recipes-json-file/blob/main/recipes.json
        try {
            String json = loadJSONFromAsset("recipe1.json");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                JSONArray ingredientsArray = jsonObject.getJSONArray("Ingredients");

                Tokenizer tokenizer = new Tokenizer();
                List<String>[] separatedIngredients = tokenizer.tokenize(ingredientsArray);

                Parser parser = new Parser();
                List<String> sortedIngredients = parser.parse(separatedIngredients);

                // Reconstruct the ingredients string
                StringBuilder ingredientsBuilder = new StringBuilder();
                for (String ingredient : sortedIngredients) {
                    ingredientsBuilder.append(ingredient).append("\n");
                }
                String ingredients = ingredientsBuilder.toString().trim();


                JSONArray methodArray = jsonObject.getJSONArray("Method");
                // Pass methodArray to Tokenizer
                List<String> mergedMethods = tokenizer.tokenizeMethods(methodArray);

                // Reconstruct the methods string
                StringBuilder methodsBuilder = new StringBuilder();
                for (String method : mergedMethods) {
                    methodsBuilder.append(method).append("\n");
                }
                String method = methodsBuilder.toString().trim();


                String recipe = name + ": \n\nIngredients - " + ingredients + ". \n\nDirections - " + method + ".";
                recipes.add(recipe);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFromCsv(){
        // Load the recipes data from the csv file
        // csv url: https://www.kaggle.com/datasets/thedevastator/better-recipes-for-a-better-life
        try {
            InputStream is = getAssets().open("recipe2m.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            //BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            reader.readLine(); // Skip the header line

            String line;
            while ((line = reader.readLine()) != null) {
                String content="";
                while(!line.equals("\""))
                {
                    content+=line;
                    content+="\n";
                    line = reader.readLine();

                }

                // Replace all the specified patterns with a unique delimiter
                content = content.replace("\",\"", "|||||")
                        .replace(",\"", "|||||")
                        .replace("\"", "|||||");
                // Now split the line using the unique delimiter
                String[] data = content.split("\\|\\|\\|\\|\\|");
                if (data.length >= 3) {
                    String name = data[0].trim(); // Trim to remove any leading/trailing whitespace
                    String ingredients = data[1].trim();
                    String method = data[2].trim();
                    String recipe = name + ": \n\nIngredients - " + ingredients + ". \n\nDirections - " + method ;
                    recipes.add(recipe);
                }
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
