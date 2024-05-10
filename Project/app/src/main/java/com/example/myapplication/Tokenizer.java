package com.example.myapplication;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public List<String>[] tokenize(JSONArray ingredientsArray) {
        List<String> numericList = new ArrayList<>();
        List<String> alphabeticalList = new ArrayList<>();

        for (int i = 0; i < ingredientsArray.length(); i++) {
            String ingredient = ingredientsArray.optString(i).trim().replace("\"", "");

            if (ingredient.matches("^[0-9].*")) {
                numericList.add(ingredient);
            } else {
                alphabeticalList.add(ingredient);
            }
        }

        List<String>[] separatedIngredients = new List[2];
        separatedIngredients[0] = numericList;
        separatedIngredients[1] = alphabeticalList;
        return separatedIngredients;
    }


    // Tokenize and merge sentences in methodArray
    public List<String> tokenizeMethods(JSONArray methodArray) {
        List<String> mergedMethods = new ArrayList<>();
        StringBuilder currentStep = new StringBuilder();

        for (int i = 0; i < methodArray.length(); i++) {
            String step = methodArray.optString(i).trim().replace("\"", "");

            // If it doesn't end with a period, append it to the current step
            if (!step.endsWith(".")) {
                currentStep.append(step).append(" ");
            } else {
                // Otherwise, append the final step and add to mergedMethods
                currentStep.append(step);
                mergedMethods.add(currentStep.toString().trim());
                currentStep.setLength(0); // Reset the StringBuilder
            }
        }

        // Add any remaining steps that were not appended
        if (currentStep.length() > 0) {
            mergedMethods.add(currentStep.toString().trim());
        }

        return mergedMethods;
    }

}
