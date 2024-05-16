package com.example.myapplication;

import com.example.myapplication.Tree.BinarySearchTree;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Unit tests for FoodDataManager class and its inner classes.
 *
 * @autor Baizhen Lin
 */
public class FoodDataManagerTest {

    private FoodDataManager foodDataManager;
    private String sampleCsvData;
    private StringWriter stringWriter;
    private FoodDataManager.DefaultReaderProvider readerProvider;
    private FoodDataManager.DefaultWriterProvider writerProvider;
    private File testFile;

    /**
     * Sets up the test environment before each test.
     */
    @Before
    public void setup() throws IOException {
        // Sample CSV data for testing
        sampleCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-05-10,1,no,User1\n" +
                "Banana,2024-05-12,1,yes,User1\n" +
                "Carrot,2024-05-11,2,no,User2";

        // StringWriter to capture changes to the sample data
        stringWriter = new StringWriter();

        // Initialize FoodDataManager with test data
        foodDataManager = new FoodDataManager(
                1, // userId
                // Reader provider using the sample CSV data
                file -> new BufferedReader(new StringReader(sampleCsvData)),
                // Writer provider updating the sample data with changes
                file -> new BufferedWriter(stringWriter) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        sampleCsvData = stringWriter.toString();  // Update sample data with the new state
                    }
                }
        );

        // Initialize Reader and Writer Providers
        readerProvider = new FoodDataManager.DefaultReaderProvider();
        writerProvider = new FoodDataManager.DefaultWriterProvider();

        // Create a temporary test file
        testFile = File.createTempFile("test", ".csv");
        testFile.deleteOnExit();

        // Write initial data to the test file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
            writer.write("Food Name,Expiry Date,UserId,IsShared,UserName\n");
            writer.write("Apple,2024-05-10,1,no,User1\n");
            writer.write("Banana,2024-05-12,1,yes,User1\n");
        }
    }

    /**
     * Tests retrieval of user-specific food data.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if a parse error occurs
     */
    @Test
    public void testGetUserData() throws IOException, ParseException {
        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        assertNotNull(userData);

        // Traverse the BST and collect the data
        StringBuilder dataBuilder = new StringBuilder();
        userData.traverseInOrder(node -> dataBuilder.append(node.foodName).append(","));
        String data = dataBuilder.toString();

        assertTrue(data.contains("Apple"));
        assertTrue(data.contains("Banana"));
    }

    /**
     * Tests retrieval of shared food data.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetSharedData() throws IOException {
        List<String[]> sharedData = foodDataManager.getSharedData(new File("dummy"));
        assertEquals(0, sharedData.size()); // UserId 1 shared items should not be included
    }

    /**
     * Tests retrieval of near-expiry food items.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if the date format is invalid
     */
    @Test
    public void testGetNearExpiryItems() throws IOException, ParseException {
        foodDataManager.sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Adjust current date to test near expiry logic
        String nearExpiryCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-05-10,1,no,User1\n" +
                "Banana,2024-05-09,1,yes,User1\n" +
                "Carrot,2024-05-08,2,no,User2";

        // Reader provider with near-expiry test data
        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(nearExpiryCsvData));

        List<String[]> nearExpiryItems = foodDataManager.getNearExpiryItems(new File("dummy"));
        assertEquals(2, nearExpiryItems.size());
        assertEquals("Banana", nearExpiryItems.get(1)[0]);
    }

    /**
     * Tests updating the shared status of a food item.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testUpdateFoodSharedStatus() throws IOException, ParseException {
        foodDataManager.updateFoodSharedStatus(new File("dummy"), "Apple");
        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        userData.traverseInOrder(node -> {
            if ("Apple".equals(node.foodName)) {
                assertEquals("yes", node.isShared);
            }
        });
    }

    /**
     * Tests deleting a food item.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if a parse error occurs
     */
    @Test
    public void testDeleteFoodItem() throws IOException, ParseException {
        foodDataManager.deleteFoodItem(new File("dummy"), "Apple");
        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        StringBuilder dataBuilder = new StringBuilder();
        userData.traverseInOrder(node -> dataBuilder.append(node.foodName).append(","));
        String data = dataBuilder.toString();

        assertFalse(data.contains("Apple"));
        assertTrue(data.contains("Banana"));
    }

    /**
     * Tests retrieval of food data for an invalid user.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if a parse error occurs
     */
    @Test
    public void testGetUserDataInvalidUser() throws IOException, ParseException {
        foodDataManager = new FoodDataManager(
                999, // Invalid userId
                file -> new BufferedReader(new StringReader(sampleCsvData)),
                file -> new BufferedWriter(stringWriter)
        );
        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        StringBuilder dataBuilder = new StringBuilder();
        userData.traverseInOrder(node -> dataBuilder.append(node.foodName).append(","));
        String data = dataBuilder.toString();

        assertTrue(data.isEmpty());
    }

    /**
     * Tests retrieval of food items not near expiry.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if the date format is invalid
     */
    @Test
    public void testGetNoNearExpiryItems() throws IOException, ParseException {
        String notNearExpiryCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-06-10,1,no,User1\n" +
                "Banana,2024-06-12,1,yes,User1\n" +
                "Carrot,2024-06-11,2,no,User2";

        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(notNearExpiryCsvData));

        List<String[]> nearExpiryItems = foodDataManager.getNearExpiryItems(new File("dummy"));
        assertEquals(0, nearExpiryItems.size());
    }

    /**
     * Tests retrieval of shared data including items shared by others.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetSharedDataIncludesOthers() throws IOException {
        String sharedDataCsv = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-05-10,1,no,User1\n" +
                "Banana,2024-05-12,1,yes,User1\n" +
                "Carrot,2024-05-11,2,yes,User2\n" +
                "Date,2024-05-12,3,yes,User3";

        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(sharedDataCsv));

        List<String[]> sharedData = foodDataManager.getSharedData(new File("dummy"));
        assertEquals(2, sharedData.size());
        assertEquals("Carrot", sharedData.get(0)[0]);
        assertEquals("Date", sharedData.get(1)[0]);
    }

    /**
     * Tests retrieval of empty user data.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if a parse error occurs
     */
    @Test
    public void testGetUserDataEmpty() throws IOException, ParseException {
        String emptyCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n";

        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(emptyCsvData));

        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        StringBuilder dataBuilder = new StringBuilder();
        userData.traverseInOrder(node -> dataBuilder.append(node.foodName).append(","));
        String data = dataBuilder.toString();

        assertTrue(data.isEmpty());
    }

    /**
     * Tests retrieval of user data with missing fields.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if a parse error occurs
     */
    @Test
    public void testGetUserDataMissingFields() throws IOException, ParseException {
        String missingFieldsCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-05-10,1,no\n" + // Missing UserName
                "Banana,2024-05-12,1,yes,User1\n";

        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(missingFieldsCsvData));

        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        StringBuilder dataBuilder = new StringBuilder();
        userData.traverseInOrder(node -> dataBuilder.append(node.foodName).append(","));
        String data = dataBuilder.toString();

        assertTrue(data.contains("Banana"));
        assertFalse(data.contains("Apple"));
    }

    /**
     * Tests updating the shared status of a non-existing food item.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if a parse error occurs
     */
    @Test
    public void testUpdateFoodSharedStatusNonExisting() throws IOException, ParseException {
        foodDataManager.updateFoodSharedStatus(new File("dummy"), "NonExistingFood");
        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        StringBuilder dataBuilder = new StringBuilder();
        userData.traverseInOrder(node -> dataBuilder.append(node.foodName).append(","));
        String data = dataBuilder.toString();

        assertTrue(data.contains("Apple"));
        assertTrue(data.contains("Banana"));
    }

    /**
     * Tests deleting a non-existing food item.
     *
     * @throws IOException if an I/O error occurs
     * @throws ParseException if a parse error occurs
     */
    @Test
    public void testDeleteFoodItemNonExisting() throws IOException, ParseException {
        foodDataManager.deleteFoodItem(new File("dummy"), "NonExistingFood");
        BinarySearchTree userData = foodDataManager.getUserData(new File("dummy"));
        StringBuilder dataBuilder = new StringBuilder();
        userData.traverseInOrder(node -> dataBuilder.append(node.foodName).append(","));
        String data = dataBuilder.toString();

        assertTrue(data.contains("Apple"));
        assertTrue(data.contains("Banana"));
    }

    /**
     * Tests DefaultReaderProvider for reading a file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testDefaultReaderProvider() throws IOException {
        BufferedReader reader = readerProvider.getBufferedReader(testFile);
        assertNotNull(reader);

        String header = reader.readLine();
        String firstLine = reader.readLine();
        String secondLine = reader.readLine();
        String thirdLine = reader.readLine();

        assertEquals("Food Name,Expiry Date,UserId,IsShared,UserName", header);
        assertEquals("Apple,2024-05-10,1,no,User1", firstLine);
        assertEquals("Banana,2024-05-12,1,yes,User1", secondLine);
        assertNull(thirdLine);

        reader.close();
    }

    /**
     * Tests DefaultWriterProvider for writing to a file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testDefaultWriterProvider() throws IOException {
        BufferedWriter writer = writerProvider.getBufferedWriter(testFile);
        assertNotNull(writer);

        writer.write("Carrot,2024-05-11,2,no,User2\n");
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        String thirdLine = reader.readLine();

        assertEquals("Carrot,2024-05-11,2,no,User2", thirdLine);

        reader.close();
    }

    /**
     * Tests DefaultReaderProvider with a directory instead of a file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test(expected = IOException.class)
    public void testDefaultReaderProviderDirectory() throws IOException {
        File directory = new File(testFile.getParent(), "test_directory");
        if (!directory.exists()) {
            directory.mkdir();
        }

        readerProvider.getBufferedReader(directory);
    }

    /**
     * Tests DefaultWriterProvider with a read-only file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test(expected = IOException.class)
    public void testDefaultWriterProviderFileNotWritable() throws IOException {
        File readOnlyFile = new File(testFile.getParent(), "read_only_file.csv");
        if (readOnlyFile.createNewFile()) {
            readOnlyFile.setReadOnly();
        }

        writerProvider.getBufferedWriter(readOnlyFile);
    }
}
