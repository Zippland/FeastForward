package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


import java.util.List;

public class RecipeSearchTest {
    private RecipeSearchManager search;

    @Before
    public void setUp() {
        search = new RecipeSearchManager ();
        search.addRecipe("Pancakes: \n\nIngredients - flour, eggs, milk, banana. \n\nDirections - Mix ingredients and cook on skillet.");
        search.addRecipe("Soup: \n\nIngredients - chicken, onion, carrots, celery. \n\nDirections - Boil ingredients until cooked.");
    }

    @Test
    public void testGetRecipesWithBanana() {
        List<String> results = search.getRecipesWithIngredient("banana");
        assertFalse("Expected non-empty result list for banana", results.isEmpty());
        assertTrue("Expected the pancakes recipe", results.contains("Pancakes: \n\nIngredients - flour, eggs, milk, banana. \n\nDirections - Mix ingredients and cook on skillet."));
    }

    @Test

    public void testGetRecipesWithOnion() {
        List<String> results = search.getRecipesWithIngredient("onion");
        assertFalse("Expected non-empty result list for onion", results.isEmpty());
        assertTrue("Expected the soup recipe", results.contains("Soup: \n\nIngredients - chicken, onion, carrots, celery. \n\nDirections - Boil ingredients until cooked."));
    }




    @Test
    public void testGetRecipesWithGarlic() {
        List<String> results = search.getRecipesWithIngredient("garlic");
        assertTrue("Expected empty result list for garlic", results.isEmpty());
    }
}