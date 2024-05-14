
/** this class is a copy of testable function from RecipeSearch to avoid problem “Method myLooper in android.os.Looper not mocked” */
package com.example.myapplication;

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
                if (ingredientItem.trim().toLowerCase().contains(inputLowerCase)) { // Simplified check
                    filteredRecipes.add(recipe);
                    break; // Stop checking further ingredients once a match is found
                }
            }
        }
        return filteredRecipes;
    }
}
