package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class TokenizerTest {

    private static class MockJSONArray extends JSONArray {
        private final List<String> items;

        public MockJSONArray(List<String> items) {
            this.items = items;
        }

        @Override
        public int length() {
            return items.size();
        }

        @Override
        public String optString(int index) {
            return items.get(index);
        }
    }

    @Test
    public void testTokenize() {
        List<String> items = Arrays.asList("1 cup flour", "2 eggs", "1/2 tsp salt", "chocolate chips");
        JSONArray mockArray = new MockJSONArray(items);

        Tokenizer tokenizer = new Tokenizer();
        List<String>[] result = tokenizer.tokenize(mockArray);

        assertEquals(Arrays.asList("1 cup flour", "2 eggs", "1/2 tsp salt"), result[0]);
        assertEquals(Arrays.asList("chocolate chips"), result[1]);
    }

    @Test
    public void testTokenizeMethods() {
        List<String> items = Arrays.asList("Preheat the oven to 350°F.", "Mix flour and eggs.", "Add chocolate chips and salt.");
        JSONArray mockArray = new MockJSONArray(items);

        Tokenizer tokenizer = new Tokenizer();
        List<String> result = tokenizer.tokenizeMethods(mockArray);

        assertEquals(Arrays.asList("Preheat the oven to 350°F.", "Mix flour and eggs.", "Add chocolate chips and salt."), result);
    }
}