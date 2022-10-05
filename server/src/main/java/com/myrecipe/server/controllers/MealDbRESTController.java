package com.myrecipe.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myrecipe.server.models.Recipe;
import com.myrecipe.server.models.RecipeSummary;
import com.myrecipe.server.services.MealDbService;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@RestController
@RequestMapping(path="/api/mealdb/", produces = MediaType.APPLICATION_JSON_VALUE)
public class MealDbRESTController {

    @Autowired
    private MealDbService mealDbSvc;

    @GetMapping(path = "/recipes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipes(@RequestParam String s) {
        List<RecipeSummary> recipes = mealDbSvc.getRecipesSummary(s);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v->{
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @GetMapping(path="/recipes/category/{categoryName}")
    public ResponseEntity<String> getRecipesSummaryByCategory(@PathVariable String categoryName) {

        List<RecipeSummary> recipes = mealDbSvc.getRecipesSummaryWithFilter("c", categoryName);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v->{
            arrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(arrayBuilder.build().toString());
    }

    @GetMapping(path="/recipes/area/{areaName}")
    public ResponseEntity<String> getRecipesSummaryByArea(@PathVariable String areaName) {
        List<RecipeSummary> recipes = mealDbSvc.getRecipesSummaryWithFilter("a", areaName);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v->{
            arrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(arrayBuilder.build().toString());
    }

    @GetMapping(path = "/recipe/{recipeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipeById(@PathVariable String recipeId) {
        Optional<Recipe> recipeOpt = mealDbSvc.getRecipeById(recipeId);
        if(recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(recipeOpt.get().toJson().toString());
        }
    }

    @GetMapping(path="/areas", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAreas() {
        List<String> areas = mealDbSvc.getAllAreas();
        if(areas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        areas.forEach(v->{
            arrBuilder.add(v);
        });
        return ResponseEntity.ok(arrBuilder.build().toString());
    }

    @GetMapping(path="/categories", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCategories() {
        List<String> cats = mealDbSvc.getAllCategories();
        if(cats.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        cats.forEach(v->{
            arrBuilder.add(v);
        });
        return ResponseEntity.ok(arrBuilder.build().toString());
    }
}
