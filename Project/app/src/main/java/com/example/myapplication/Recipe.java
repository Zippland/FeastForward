package com.example.myapplication;

public class Recipe {
    private String title;
    private String ingredients;
    private String directions;
    private String link;
    private String source;
    private String ner;
    private String expireDate; // 新添加的过期日期属性
    private int userId; // 新添加的用户编号属性

    public Recipe() {
    }

    public Recipe(String title, String ingredients, String directions, String link, String source, String ner, String expireDate, int userId) {
        this.title = title;
        this.ingredients = ingredients;
        this.directions = directions;
        this.link = link;
        this.source = source;
        this.ner = ner;
        this.expireDate = expireDate;
        this.userId = userId;
    }



    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getDirections() {
        return directions;
    }

    public String getLink() {
        return link;
    }

    public String getSource() {
        return source;
    }

    public String getNer() {
        return ner;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public int getUserId() {
        return userId;
    }

    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setNer(String ner) {
        this.ner = ner;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\n" +
                "Ingredients: " + ingredients + "\n" +
                "Directions: " + directions + "\n" +
                "Link: " + link + "\n" +
                "Source: " + source + "\n" +
                "NER: " + ner + "\n" +
                "Expire Date: " + expireDate + "\n" +
                "User ID: " + userId + "\n";
    }
}
