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

public class FoodDataManagerTest {

    private FoodDataManager foodDataManager;
    private String sampleCsvData;
    private StringWriter stringWriter;

    @Before
    public void setup() {
        sampleCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-05-10,1,no,User1\n" +
                "Banana,2024-05-12,1,yes,User1\n" +
                "Carrot,2024-05-11,2,no,User2";

        stringWriter = new StringWriter();

        foodDataManager = new FoodDataManager(
                1, // userId
                file -> new BufferedReader(new StringReader(sampleCsvData)),
                file -> new BufferedWriter(stringWriter) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        sampleCsvData = stringWriter.toString();  // Update sample data with the new state
                    }
                }
        );
    }

    @Test
    public void testGetUserData() throws IOException {
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(2, userData.size());
        assertEquals("Apple", userData.get(0)[0]);
        assertEquals("Banana", userData.get(1)[0]);
    }

    @Test
    public void testGetSharedData() throws IOException {
        List<String[]> sharedData = foodDataManager.getSharedData(new File("dummy"));
        assertEquals(0, sharedData.size()); // UserId 1 shared items should not be included
    }

    @Test
    public void testGetNearExpiryItems() throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        foodDataManager.sdf = sdf;

        // Adjust current date to test near expiry logic
        String nearExpiryCsvData = "Food Name,Expiry Date,UserId,IsShared,UserName\n" +
                "Apple,2024-05-10,1,no,User1\n" +
                "Banana,2024-05-09,1,yes,User1\n" +
                "Carrot,2024-05-08,2,no,User2";

        foodDataManager.readerProvider = file -> new BufferedReader(new StringReader(nearExpiryCsvData));

        List<String[]> nearExpiryItems = foodDataManager.getNearExpiryItems(new File("dummy"));
        assertEquals(2, nearExpiryItems.size());
        assertEquals("Banana", nearExpiryItems.get(1)[0]);
    }

    @Test
    public void testUpdateFoodSharedStatus() throws IOException {
        foodDataManager.updateFoodSharedStatus(new File("dummy"), "Apple");
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals("yes", userData.get(0)[3]);
    }

    @Test
    public void testDeleteFoodItem() throws IOException {
        foodDataManager.deleteFoodItem(new File("dummy"), "Apple");
        List<String[]> userData = foodDataManager.getUserData(new File("dummy"));
        assertEquals(1, userData.size());
        assertEquals("Banana", userData.get(0)[0]);
    }
}
