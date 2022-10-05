package com.myrecipe.server.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class RecipeSummary {
    private String recipeId;
    private String name;
    private String thumbnail;
    public String getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public static RecipeSummary convert(JsonObject jsonObject) {
        RecipeSummary recipeSummary = new RecipeSummary();
        recipeSummary.setRecipeId(jsonObject.getString("idMeal", ""));
        recipeSummary.setName(jsonObject.getString("strMeal", ""));
        recipeSummary.setThumbnail(jsonObject.getString("strMealThumb", ""));
        return recipeSummary;
    }

    public static RecipeSummary convert(final SqlRowSet rs) {
        RecipeSummary recipeSummary = new RecipeSummary();
        recipeSummary.setRecipeId(String.valueOf(rs.getInt("recipe_id")));
        recipeSummary.setName(rs.getString("name"));
        recipeSummary.setThumbnail(rs.getString("thumbnail"));
        return recipeSummary;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
        .add("recipeId", recipeId)
        .add("name", name)
        .add("thumbnail", thumbnail)
        .build();
    }
}
