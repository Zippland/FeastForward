/**
 * DESCRIPTION: This class is a copy of testable functions from RecipeSearch to avoid problem “Method myLooper in android.os.Looper not mocked”
 * CONTRIBUTION: All methods are created by Zhongyi Ding.
 * @author Zhongyi Ding u7619253
 *
 */

package com.example.myapplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecipeSearchManager {
    private List<String> recipes = new ArrayList<>();

    public void addRecipe(String recipe) {
        recipes.add(recipe);
    }

    public List<String> getRecipesWithIngredient(String ingredient) {
        List<String> filteredRecipes = new ArrayList<>();
        String inputLowerCase = ingredient.toLowerCase().trim();

        for (String recipe : recipes) {
            int start = recipe.indexOf("Ingredients - ") + "Ingredients - ".length();
            int end = recipe.indexOf(". ", start);
            if (end == -1) end = recipe.length(); // Handling case where period might not be present.
            String ingredientsText = recipe.substring(start, end);
            String[] ingredients = ingredientsText.split(", ");

            for (String ingredientItem : ingredients) {
                if (calculateSimilarity(ingredientItem.trim().toLowerCase(), inputLowerCase) >= 0.5) {  // Adjust similarity threshold as needed
                    filteredRecipes.add(recipe);
                    break; // Stop checking further ingredients once a match is found
                }
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




}
