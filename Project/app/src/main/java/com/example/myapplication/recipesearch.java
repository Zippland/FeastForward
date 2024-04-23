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
import java.util.ArrayList;
import java.util.List;
public class recipesearch extends AppCompatActivity {

    // Define the ArrayList of recipes
    private ArrayList<String> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipesearch);

        // Add some recipes to the ArrayList
        recipes.add("Chicken Soup: Ingredients - chicken, salt, water. Directions - mix them together and boil.");
        recipes.add("Vegetable Salad: Ingredients - lettuce, tomato, cucumber. Directions - chop and mix them together.");
        recipes.add("Fruit Salad: Ingredients - apple, banana, orange. Directions - slice and mix them together.");
        recipes.add("Pasta Bolognese: Ingredients - pasta, ground beef, tomato sauce. Directions - cook pasta and ground beef separately, then mix with tomato sauce.");
        recipes.add("Cheese Pizza: Ingredients - pizza dough, tomato sauce, mozzarella cheese. Directions - spread tomato sauce and cheese on dough, then bake.");
        recipes.add("Beef Tacos: Ingredients - taco shells, ground beef, lettuce, tomato, cheese. Directions - cook beef, then assemble tacos with beef and toppings.");
        recipes.add("Chicken Stir Fry: Ingredients - chicken, bell peppers, soy sauce. Directions - stir fry chicken and bell peppers, then add soy sauce.");
        recipes.add("Veggie Burger: Ingredients - veggie patty, bun, lettuce, tomato. Directions - cook veggie patty, then assemble burger with patty and toppings.");
        recipes.add("Fish and Chips: Ingredients - fish, potatoes, oil. Directions - fry fish and chips separately, then serve together.");
        recipes.add("Mushroom Risotto: Ingredients - arborio rice, mushrooms, chicken broth. Directions - cook rice and mushrooms separately, then mix with broth.");
        recipes.add("Tomato Soup: Ingredients - tomatoes, salt, cream. Directions - blend tomatoes, then heat with salt and cream.");
        recipes.add("Apple Pie: Ingredients - apples, sugar, pie crust. Directions - mix apples and sugar, then bake in pie crust.");
        recipes.add("Chocolate Cake: Ingredients - flour, sugar, cocoa powder, eggs. Directions - mix ingredients and bake.");
        recipes.add("Grilled Cheese Sandwich: Ingredients - bread, cheese, butter. Directions - butter bread, place cheese between slices, grill until golden.");
        recipes.add("Pancakes: Ingredients - flour, eggs, milk. Directions - mix ingredients, pour onto hot griddle, flip when bubbles form.");
        recipes.add("Scrambled Eggs: Ingredients - eggs, milk, butter. Directions - whisk eggs and milk, cook in buttered pan, stirring frequently.");
        recipes.add("Spaghetti Carbonara: Ingredients - spaghetti, eggs, bacon, parmesan cheese. Directions - cook spaghetti and bacon separately, mix with beaten eggs and cheese.");
        recipes.add("Roast Chicken: Ingredients - chicken, salt, pepper, olive oil. Directions - season chicken, roast in oven until cooked through.");
        recipes.add("Beef Stew: Ingredients - beef, potatoes, carrots, onions, beef broth. Directions - brown beef, add vegetables and broth, simmer until tender.");
        // Get the EditText, Button, and TextView from the layout
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

                // Display the first recipe in the TextView
                if (!filteredRecipes.isEmpty()) {
                    recipeTextView.setText(filteredRecipes.get(0));
                } else {
                    recipeTextView.setText("No recipes found with the entered ingredient.");
                }
            }
        });
    }

    // Method to get recipes with a certain ingredient
    private List<String> getRecipesWithIngredient(String ingredient) {
        List<String> filteredRecipes = new ArrayList<>();
        for (String recipe : recipes) {
            if (recipe.contains(ingredient)) {
                filteredRecipes.add(recipe);
            }
        }
        return filteredRecipes;
    }
}