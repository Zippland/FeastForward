package com.example.myapplication.Utils;

import android.content.Context;

import com.example.myapplication.FileHelper;
import com.example.myapplication.Recipe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DataUtil {
    private static File file;

    public static void init(Context context) {
        file = new File(context.getFilesDir(), "dataset.csv");
    }

    //write data to dataset
    public static void writeEntryToDataset(String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(entry);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Recipe> parseCsv() {
        List<Recipe> result = new ArrayList<>();
        if (!file.exists()) {
            Recipe recipe = new Recipe();
            recipe.setTitle("File does not exist");
            recipe.setExpireDate("2099-12-31");
            recipe.setUserId(1);
            result.add(recipe);
            return result;
        }
        try {
            String[] lines = FileHelper.readFileLines(file);
            if (lines.length < 1) {
                Recipe recipe = new Recipe();
                recipe.setTitle("You have not added any data");
                recipe.setExpireDate("2099-12-31");
                recipe.setUserId(1);
                result.add(recipe);
                return result;
            }
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Recipe recipe = new Recipe();
                    recipe.setTitle(parts[0]);
                    recipe.setExpireDate(parts[1]);
                    recipe.setUserId(Integer.parseInt(parts[2]));
                    result.add(recipe);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //delete data from dataset
    public static void deleteRecipe(int lineNumber) {
        Path path = Paths.get(file.toURI()); // dataset
        try {
            List<String> lines = Files.readAllLines(path);
            if (lines.size() > lineNumber) {
                lines.remove(lineNumber);
            }
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
