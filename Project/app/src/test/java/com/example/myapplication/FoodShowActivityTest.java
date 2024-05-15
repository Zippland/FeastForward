package com.example.myapplication;

import com.example.myapplication.Utils.DataUtil; // Add import statement

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FoodShowActivityTest {

    private FoodShowActivity activity;
    private DataUtilMock dataUtilMock;

    @Before
    public void setUp() {
        activity = new FoodShowActivity();
        activity.init(); // Initialize method

        // Create an instance of DataUtilMock
        dataUtilMock = new DataUtilMock();
    }

    @Test
    public void testInit() {
        assertNotNull(activity.recipesList);
        assertNotNull(activity.recipeAdapter);
    }

    @Test
    public void testDeleteRecipe() {
        int initialSize = activity.recipeAdapter.getItemCount();
        invokePrivateMethod(activity, "showContextMenu", 0);
        assertEquals(initialSize - 1, activity.recipeAdapter.getItemCount());
    }

    @Test
    public void testShowContextMenu() {
        int position = 1; // Simulate long press position
        invokePrivateMethod(activity, "showContextMenu", position);
        assertEquals("Recipe 2", DataUtilMock.deletedRecipeTitle); // Verify if DataUtil.deleteRecipe method is called and with correct arguments
    }

    // Invoke private method using reflection
    private void invokePrivateMethod(Object target, String methodName, Object... args) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, int.class);
            method.setAccessible(true);
            method.invoke(target, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // Mock DataUtil class
    private static class DataUtilMock extends DataUtil {
        static String deletedRecipeTitle;

        // Change method to public
        public static void deleteRecipe(String recipeTitle) {
            deletedRecipeTitle = recipeTitle;
        }
    }
}
