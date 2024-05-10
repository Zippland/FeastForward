package com.example.myapplication;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Parser {
    public List<String> parse(List<String>[] separatedIngredients) {
        List<String> numericList = separatedIngredients[0];
        List<String> alphabeticalList = separatedIngredients[1];

        // Use sets to remove duplicates
        Set<String> numericSet = new HashSet<>(numericList);
        Set<String> alphabeticalSet = new HashSet<>(alphabeticalList);

        // Sort numeric list
        List<String> sortedNumericList = numericSet.stream()
                .sorted()
                .collect(Collectors.toList());

        // Capitalize and sort alphabetical list
        List<String> sortedAlphabeticalList = alphabeticalSet.stream()
                .map(String::toLowerCase)
                .map(ingredient -> Character.toUpperCase(ingredient.charAt(0)) + ingredient.substring(1))
                .sorted()
                .collect(Collectors.toList());

        // Combine both lists
        List<String> sortedIngredients = sortedNumericList;
        sortedIngredients.addAll(sortedAlphabeticalList);
        return sortedIngredients;
    }
}

