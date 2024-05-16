package com.example.myapplication;

import com.example.myapplication.Tree.BinarySearchTree;

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

/**
 * FoodDataManager is responsible for managing food data, including retrieving user-specific data,
 * shared data, near expiry items, and updating or deleting food items.
 *
 * @author Baizhen Lin u7770074
 */
public class FoodDataManager {

    public int userId;
    public ReaderProvider readerProvider;
    public WriterProvider writerProvider;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private BinarySearchTree bst;

    /**
     * Constructor to initialize FoodDataManager.
     *
     * @param userId         the user ID of the current user
     * @param readerProvider the provider for BufferedReader
     * @param writerProvider the provider for BufferedWriter
     */
    public FoodDataManager(int userId, ReaderProvider readerProvider, WriterProvider writerProvider) {
        this.userId = userId;
        this.readerProvider = readerProvider;
        this.writerProvider = writerProvider;
        this.bst = new BinarySearchTree();
    }

    /**
     * Retrieves the food data for the current user from the specified file.
     *
     * @param foodDataFile the file containing food data
     * @return a binary search tree with food data entries for the user
     * @throws IOException if an I/O error occurs
     */
    public BinarySearchTree getUserData(File foodDataFile) throws IOException, ParseException {
        BufferedReader reader = null;
        try {
            reader = readerProvider.getBufferedReader(foodDataFile);
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 5) {
                    String readUserId = tokens[2];
                    if (userId == Integer.parseInt(readUserId)) {
                        bst.insert(tokens[0], tokens[1], tokens[3]);
                    }
                }
            }
            return bst;
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

    /**
     * Retrieves the shared food data from the specified file.
     *
     * @param foodDataFile the file containing food data
     * @return a list of shared food data entries
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Retrieves the food items that are near expiry for the current user from the specified file.
     *
     * @param foodDataFile the file containing food data
     * @return a list of near expiry food items
     * @throws IOException if an I/O error occurs
     * @throws ParseException if the date format is invalid
     */
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
                        if (expiryDate != null && (expiryDate.getTime() - now) <= threeDaysInMs) {
                            nearExpiryItems.add(tokens);
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

    /**
     * Updates the shared status of a food item for the current user in the specified file.
     *
     * @param foodDataFile the file containing food data
     * @param foodName     the name of the food item to update
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Deletes a food item for the current user from the specified file.
     *
     * @param foodDataFile the file containing food data
     * @param foodName     the name of the food item to delete
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Interface for providing BufferedReader.
     */
    public interface ReaderProvider {
        BufferedReader getBufferedReader(File file) throws IOException;
    }

    /**
     * Interface for providing BufferedWriter.
     */
    public interface WriterProvider {
        BufferedWriter getBufferedWriter(File file) throws IOException;
    }

    /**
     * Default implementation of ReaderProvider.
     */
    public static class DefaultReaderProvider implements ReaderProvider {
        @Override
        public BufferedReader getBufferedReader(File file) throws IOException {
            return new BufferedReader(new java.io.FileReader(file));
        }
    }

    /**
     * Default implementation of WriterProvider.
     */
    public static class DefaultWriterProvider implements WriterProvider {
        @Override
        public BufferedWriter getBufferedWriter(File file) throws IOException {
            return new BufferedWriter(new java.io.FileWriter(file, false));
        }
    }

}
