package com.myrecipe.server.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.myrecipe.server.models.Recipe;
import com.myrecipe.server.models.RecipeSummary;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class MealDbService {

    private static final String URL_SEARCH_NAME = "https://www.themealdb.com/api/json/v1/1/search.php";
    private static final String URL_SEARCH_ID = "https://www.themealdb.com/api/json/v1/1/lookup.php";
    private static final String URL_CATEGORIES = "https://www.themealdb.com/api/json/v1/1/list.php?c=list";
    private static final String URL_AREAS = "https://www.themealdb.com/api/json/v1/1/list.php?a=list";
    private static final String URL_FILTER = "https://www.themealdb.com/api/json/v1/1/filter.php";

    public List<RecipeSummary> getRecipesSummary(String query) {
        final String searchUrl = UriComponentsBuilder.fromUriString(URL_SEARCH_NAME)
                .queryParam("s", query)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> req = RequestEntity.get(searchUrl)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        final String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray jRecipes = null;
        List<RecipeSummary> recipes = new ArrayList<RecipeSummary>();
        try {
            jRecipes = reader.readObject().getJsonArray("meals");
            for (int i = 0; i < jRecipes.size(); i++) {
                RecipeSummary r = RecipeSummary.convert(jRecipes.getJsonObject(i));
                recipes.add(r);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return recipes;
    }

    public Optional<Recipe> getRecipeById(String recipedId) {

        final String searchUrl = UriComponentsBuilder.fromUriString(URL_SEARCH_ID)
                .queryParam("i", recipedId)
                .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> req = RequestEntity.get(searchUrl)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        final String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray jRecipes = null;
        try {
            jRecipes = reader.readObject().getJsonArray("meals");
        } catch (ClassCastException ex) {
            return Optional.empty();
        }

        Recipe recipe = Recipe.convert(jRecipes.getJsonObject(0));

        return Optional.of(recipe);
    }

    public List<String> getAllAreas() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.getForEntity(URL_AREAS, String.class);
        final String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray categories = null;
        try {
            categories = reader.readObject().getJsonArray("meals");
        } catch (ClassCastException ex) {
            return new ArrayList<>();
        }

        List<String> areaList = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            JsonObject obj = categories.getJsonObject(i);
            areaList.add(obj.getString("strArea"));
        }
        return areaList;
    }

    public List<String> getAllCategories() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.getForEntity(URL_CATEGORIES, String.class);
        final String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray categories = null;
        try {
            categories = reader.readObject().getJsonArray("meals");
        } catch (ClassCastException ex) {
            return new ArrayList<>();
        }

        List<String> categoryList = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            JsonObject obj = categories.getJsonObject(i);
            categoryList.add(obj.getString("strCategory"));
        }
        return categoryList;
    }

    public List<RecipeSummary> getRecipesSummaryWithFilter(final String filterBy, final String filterValue) {
        final String searchUrl = UriComponentsBuilder.fromUriString(URL_FILTER)
                .queryParam(filterBy, filterValue)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> req = RequestEntity.get(searchUrl)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        final String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonArray jRecipes = null;
        
        try {

            List<RecipeSummary> recipes = new ArrayList<>();
            jRecipes = reader.readObject().getJsonArray("meals");
            if (jRecipes == null)
                return recipes;

            for (int i = 0; i < jRecipes.size(); i++) {
                RecipeSummary r = RecipeSummary.convert(jRecipes.getJsonObject(i));
                recipes.add(r);
            }

            return recipes;
            
        } catch (ClassCastException ex) {
            return new ArrayList<>();
        }
    }
}
