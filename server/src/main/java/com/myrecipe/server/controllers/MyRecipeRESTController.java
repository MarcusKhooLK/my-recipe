package com.myrecipe.server.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myrecipe.server.models.Recipe;
import com.myrecipe.server.models.RecipeSummary;
import com.myrecipe.server.models.Response;
import com.myrecipe.server.services.MyRecipeService;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api/myrecipe", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyRecipeRESTController {

    @Autowired
    private MyRecipeService myRecipeSvc;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createRecipe(@RequestPart("thumbnail") MultipartFile file,
                                                @RequestPart String name,
                                                @RequestPart String category,
                                                @RequestPart String area,
                                                @RequestPart String instructions,
                                                @RequestPart String youtubeLink,
                                                @RequestPart String ingredients,
                                                @RequestPart String measurements,
                                                @RequestPart String email) {
       Recipe r = new Recipe();
        r.setName(name);
        r.setCategory(category);
        r.setCountry(area);
        r.setInstructions(instructions);
        r.setYoutubeLink(youtubeLink);
        List<String> ingredientsList = Arrays.asList(ingredients.split(","));
        List<String> measurementsList = Arrays.asList(measurements.split(","));
        r.setIngredients(ingredientsList);
        r.setMeasurements(measurementsList);
        r.setThumbnail("./assets/test/placeholder.png");
        int recipeId = myRecipeSvc.createRecipe(r, email);

        if(recipeId > 0) {
            Response resp = new Response();
            resp.setCode(HttpStatus.CREATED.value());
            resp.setMessage("Recipe created");
            JsonObject data = Json.createObjectBuilder().add("recipeId", recipeId).build();
            resp.setData(data);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp.toJson().toString());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/recipe/{recipeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipeById(@PathVariable String recipeId) {
        Optional<Recipe> recipeOpt = myRecipeSvc.getRecipeById(recipeId);
        if(recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(recipeOpt.get().toJson().toString());
        }
    }

    @GetMapping(path="/recipes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByEmail(@RequestParam String email) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByEmail(email);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v->{
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @GetMapping(path = "/recipes/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByName(@PathVariable String name) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByName(name);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v->{
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @GetMapping(path = "/recipes/category/{cat}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByCategory(@PathVariable String cat) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByCategory(cat);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v->{
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @GetMapping(path = "/recipes/area/{area}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByArea(@PathVariable String area) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByArea(area);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v->{
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }
}
