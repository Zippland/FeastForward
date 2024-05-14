package com.example.myapplication;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParserTest {

    private Parser parser;

    @Before
    public void setUp() {
        parser = new Parser();
    }

    @Test
    public void testParse() {

        List<String> numericList = new ArrayList<>();
        numericList.add("2 cups flour");
        numericList.add("1 cup sugar");
        numericList.add("2 eggs");
        numericList.add("1 cup milk");

        List<String> alphabeticalList = new ArrayList<>();
        alphabeticalList.add("salt");
        alphabeticalList.add("pepper");
        alphabeticalList.add("vanilla extract");
        alphabeticalList.add("butter");

        // input
        List<String>[] separatedIngredients = new List[2];
        separatedIngredients[0] = numericList;
        separatedIngredients[1] = alphabeticalList;

        // do parser
        List<String> result = parser.parse(separatedIngredients);

        // expect result
        List<String> expected = new ArrayList<>();
        expected.add("1 cup milk");
        expected.add("1 cup sugar");
        expected.add("2 cups flour");
        expected.add("2 eggs");
        expected.add("Butter");
        expected.add("Pepper");
        expected.add("Salt");
        expected.add("Vanilla extract");

        // test
        Assert.assertEquals(expected, result);
    }
}