package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Unit tests for FoodDataManager class.
 *
 * @autor Baizhen Lin
 */
public class FoodDataManagerTest {

    private FoodDataManager foodDataManager;
    private String sampleCsvData;
    private StringWriter stringWriter;

    /**
     * Sets up the test environment before each test.
     */
    @Before
    public void setup() {
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
    }

    /**
     * Tests retrieval of user-specific food data.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetUserData() throws IOException {
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(2, userData.size());
        assertEquals("Apple", userData.get(0)[0]);
        assertEquals("Banana", userData.get(1)[0]);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        foodDataManager.sdf = sdf;

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
    public void testUpdateFoodSharedStatus() throws IOException {
        foodDataManager.updateFoodSharedStatus(new File("dummy"), "Apple");
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals("yes", userData.get(0)[3]);
    }

    /**
     * Tests deleting a food item.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testDeleteFoodItem() throws IOException {
        foodDataManager.deleteFoodItem(new File("dummy"), "Apple");
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(1, userData.size());
        assertEquals("Banana", userData.get(0)[0]);
    }

    /**
     * Tests retrieval of food data for an invalid user.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetUserDataInvalidUser() throws IOException {
        foodDataManager = new FoodDataManager(
                999, // Invalid userId
                file -> new BufferedReader(new StringReader(sampleCsvData)),
                file -> new BufferedWriter(stringWriter)
        );
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(0, userData.size());
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
     */
    @Test
    public void testGetUserDataEmpty() throws IOException {
        String emptyCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n";

        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(emptyCsvData));

        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(0, userData.size());
    }

    /**
     * Tests retrieval of user data with missing fields.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetUserDataMissingFields() throws IOException {
        String missingFieldsCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-05-10,1,no\n" + // Missing UserName
                "Banana,2024-05-12,1,yes,User1\n";

        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(missingFieldsCsvData));

        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(1, userData.size());
        assertEquals("Banana", userData.get(0)[0]);
    }

    /**
     * Tests updating the shared status of a non-existing food item.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testUpdateFoodSharedStatusNonExisting() throws IOException {
        foodDataManager.updateFoodSharedStatus(new File("dummy"), "NonExistingFood");
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(2, userData.size());
        assertEquals("no", userData.get(0)[3]);
        assertEquals("yes", userData.get(1)[3]);
    }

    /**
     * Tests deleting a non-existing food item.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testDeleteFoodItemNonExisting() throws IOException {
        foodDataManager.deleteFoodItem(new File("dummy"), "NonExistingFood");
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(2, userData.size());
        assertEquals("Apple", userData.get(0)[0]);
        assertEquals("Banana", userData.get(1)[0]);
    }
}
