package com.myrecipe.server.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Recipe extends RecipeSummary{
    
    private String category = "";
    private String country = "";
    private String instructions = "";
    private String youtubeLink = "";
    private String createdBy = "";
    private List<String> ingredients = new ArrayList<String>();
    private List<String> measurements = new ArrayList<String>();

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public String getYoutubeLink() {
        return youtubeLink;
    }
    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public List<String> getMeasurements() {
        return measurements;
    }
    public void setMeasurements(List<String> measurements) {
        this.measurements = measurements;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // convert from mealdb
    public static Recipe convert(JsonObject jsonObject) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(jsonObject.getString("idMeal", ""));
        recipe.setName(jsonObject.getString("strMeal", ""));
        recipe.setThumbnail(jsonObject.getString("strMealThumb", ""));
        recipe.setCategory(jsonObject.getString("strCategory", ""));
        recipe.setCountry(jsonObject.getString("strArea", ""));
        recipe.setInstructions(jsonObject.getString("strInstructions", ""));
        String youtubeLink = jsonObject.getString("strYoutube", "");
        //youtubeLink = youtubeLink.replace("/watch?v=", "/embed/");
        recipe.setYoutubeLink(youtubeLink);
        recipe.setCreatedBy("MealDB");
        List<String> ingredients = new ArrayList<String>();
        List<String> measurements = new ArrayList<String>();
        for(int i = 1; i <= 20; i++) {
            String temp = jsonObject.getString("strIngredient%d".formatted(i), "");
            if(!temp.isEmpty())
                ingredients.add(temp);
            
            temp = jsonObject.getString("strMeasure%d".formatted(i), "");
            if(!temp.isEmpty())
                measurements.add(temp);
        }
        recipe.setIngredients(ingredients);
        recipe.setMeasurements(measurements);
        return recipe;
    }

    public JsonObject toJson() {
        JsonArrayBuilder ingredientsBuilder = Json.createArrayBuilder();
        ingredients.forEach(v->{
            ingredientsBuilder.add(v);
        });
        JsonArrayBuilder measurementBuilder = Json.createArrayBuilder();
        measurements.forEach(v->{
            measurementBuilder.add(v);
        });
        return Json.createObjectBuilder()
                .add("recipeId", getRecipeId())
                .add("name", getName())
                .add("thumbnail", getThumbnail())
                .add("category", category)
                .add("country", country)
                .add("instructions", instructions)
                .add("youtubeLink", (youtubeLink == null) ? "" : youtubeLink)
                .add("createdBy", createdBy)
                .add("ingredients", ingredientsBuilder)
                .add("measurements", measurementBuilder)
                .build();
    }

    public static Recipe convert(final SqlRowSet result) {
        Recipe r = new Recipe();
        r.setRecipeId(String.valueOf(result.getInt("recipe_id")));
        r.setName(result.getString("name"));
        r.setCategory(result.getString("category"));
        r.setCountry(result.getString("country"));
        r.setThumbnail(result.getString("thumbnail"));
        r.setYoutubeLink(result.getString("youtubeLink"));
        r.setInstructions(result.getString("instructions"));
        return r;
    }
}
