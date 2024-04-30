package com.example.myapplication;

public class Recipe {
    private String title;
    private String ingredients;
    private String directions;
    private String link;
    private String source;
    private String ner;

    public Recipe() {
    }

    public Recipe(String title, String ingredients, String directions, String link, String source, String ner) {
        this.title = title;
        this.ingredients = ingredients;
        this.directions = directions;
        this.link = link;
        this.source = source;
        this.ner = ner;
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

    @Override
    public String toString() {
        return "Title: " + title + "\n" +
                "Ingredients: " + ingredients + "\n" +
                "Directions: " + directions + "\n" +
                "Link: " + link + "\n" +
                "Source: " + source + "\n" +
                "NER: " + ner + "\n";
    }
}
