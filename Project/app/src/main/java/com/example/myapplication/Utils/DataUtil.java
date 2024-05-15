package com.example.myapplication.Utils;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.FileHelper;
import com.example.myapplication.R;
import com.example.myapplication.Recipe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
        try {
            InputStream foodCsv = context.getResources().openRawResource(R.raw.food_data);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = foodCsv.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.write(new byte[]{'\n'});
            outputStream.flush();
            foodCsv.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //write data to dataset
    public static void writeEntryToDataset(String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String lowerCase = String.valueOf(entry.toCharArray()[0]);
            String upperCase = String.valueOf(lowerCase.toUpperCase().toCharArray()[0]);
            entry = entry.replaceFirst(lowerCase, upperCase);
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
                    try {
                        recipe.setUserId(Integer.parseInt(parts[2]));
                    } catch (NumberFormatException e) {
                        //Error data, skip search
                        continue;
                    }
                    result.add(recipe);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //delete data from dataset
    public static void deleteRecipe(String foodName) {
        Path path = Paths.get(file.toURI()); // dataset
        try {
            List<String> lines = Files.readAllLines(path);
            Log.d("Test", "deleteRecipe: "+lines.size());
            for (int i = 0; i < lines.size(); i++) {
                String fN = lines.get(i).split(",")[0].trim();
                if (fN.equals(foodName)) {
                    lines.remove(i);
                    break;
                }
            }
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
