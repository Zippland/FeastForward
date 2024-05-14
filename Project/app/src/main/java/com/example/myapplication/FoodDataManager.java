package com.example.myapplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodDataManager {

    public int userId;
    public ReaderProvider readerProvider;
    public WriterProvider writerProvider;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public FoodDataManager(int userId, ReaderProvider readerProvider, WriterProvider writerProvider) {
        this.userId = userId;
        this.readerProvider = readerProvider;
        this.writerProvider = writerProvider;
    }

    public List<String[]> getUserData(File foodDataFile) throws IOException {
        BufferedReader reader = null;
        try {
            reader = readerProvider.getBufferedReader(foodDataFile);
            List<String[]> userData = new ArrayList<>();
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 5) {
                    String readUserId = tokens[2];
                    if (userId == Integer.parseInt(readUserId)) {
                        userData.add(tokens);
                    }
                }
            }
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error reading user data", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String[]> getSharedData(File foodDataFile) throws IOException {
        BufferedReader reader = null;
        try {
            reader = readerProvider.getBufferedReader(foodDataFile);
            List<String[]> sharedData = new ArrayList<>();
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 5 && "yes".equalsIgnoreCase(tokens[3]) && userId != Integer.parseInt(tokens[2])) {
                    sharedData.add(tokens);
                }
            }
            return sharedData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error reading shared data", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String[]> getNearExpiryItems(File foodDataFile) throws IOException, ParseException {
        BufferedReader reader = null;
        try {
            reader = readerProvider.getBufferedReader(foodDataFile);
            List<String[]> nearExpiryItems = new ArrayList<>();
            String line;
            reader.readLine(); // Skip header

            long now = new Date().getTime();
            long threeDaysInMs = 3 * 24 * 3600 * 1000;  // 3 days in milliseconds

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    String readUserId = tokens[2];
                    if (userId == Integer.parseInt(readUserId)) {
                        Date expiryDate = sdf.parse(tokens[1]);
                        System.out.println("Expiry date for " + tokens[0] + ": " + expiryDate);
                        System.out.println("Current date: " + new Date(now));
                        if (expiryDate != null && (expiryDate.getTime() - now) <= threeDaysInMs) {
                            nearExpiryItems.add(tokens);
                            System.out.println("Added near expiry item: " + tokens[0]);
                        }
                    }
                }
            }
            return nearExpiryItems;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error reading near expiry items", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateFoodSharedStatus(File foodDataFile, String foodName) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = readerProvider.getBufferedReader(foodDataFile);
            List<String> lines = new ArrayList<>();
            String line;
            lines.add(reader.readLine()); // Preserve header

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 5 && tokens[0].equals(foodName)) {
                    tokens[3] = "yes";
                    line = String.join(",", tokens);
                }
                lines.add(line);
            }
            reader.close();
            reader = null;

            writer = writerProvider.getBufferedWriter(foodDataFile);
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error updating food shared status", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteFoodItem(File foodDataFile, String foodName) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = readerProvider.getBufferedReader(foodDataFile);
            List<String> lines = new ArrayList<>();
            String line;
            lines.add(reader.readLine()); // Preserve header

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 4 && !tokens[0].equals(foodName)) {
                    lines.add(line);
                }
            }
            reader.close();
            reader = null;

            writer = writerProvider.getBufferedWriter(foodDataFile);
            for (String remainingLine : lines) {
                writer.write(remainingLine);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error deleting food item", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface ReaderProvider {
        BufferedReader getBufferedReader(File file) throws IOException;
    }

    public interface WriterProvider {
        BufferedWriter getBufferedWriter(File file) throws IOException;
    }

    public static class DefaultReaderProvider implements ReaderProvider {
        @Override
        public BufferedReader getBufferedReader(File file) throws IOException {
            return new BufferedReader(new java.io.FileReader(file));
        }
    }

    public static class DefaultWriterProvider implements WriterProvider {
        @Override
        public BufferedWriter getBufferedWriter(File file) throws IOException {
            return new BufferedWriter(new java.io.FileWriter(file, false));
        }
    }
}
