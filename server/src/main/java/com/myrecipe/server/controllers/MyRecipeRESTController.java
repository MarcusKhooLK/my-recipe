package com.myrecipe.server.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myrecipe.server.EmailDetails;
import com.myrecipe.server.constants.EmailTemplate;
import com.myrecipe.server.constants.URLs;
import com.myrecipe.server.models.Recipe;
import com.myrecipe.server.models.RecipeSummary;
import com.myrecipe.server.models.Response;
import com.myrecipe.server.services.EmailService;
import com.myrecipe.server.services.MyRecipeService;
import com.myrecipe.server.services.S3Service;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api/myrecipe", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyRecipeRESTController {

    @Autowired
    private MyRecipeService myRecipeSvc;

    @Autowired
    private S3Service s3Svc;

    @Autowired
    private EmailService emailSvc;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createRecipe(@RequestPart("thumbnail") MultipartFile file,
            @RequestPart String name,
            @RequestPart String category,
            @RequestPart String area,
            @RequestPart String instructions,
            @RequestPart(name = "youtubeLink", required = false) String youtubeLink,
            @RequestPart String ingredients,
            @RequestPart String measurements,
            @RequestPart String email) {

        Response resp = new Response();

        // S3 is disabled, I will use local mysql below instead
        // Optional<String> thumbnailOpt = s3Svc.upload(file, email);
        // if (thumbnailOpt.isEmpty()) {
        //     resp.setCode(HttpStatus.BAD_REQUEST.value());
        //     resp.setMessage("Something went wrong when creating recipe! Failed to upload thumbnail");
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp.toJson().toString());
        // }

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
        //r.setThumbnail(thumbnailOpt.get());

        try {
            int recipeId = myRecipeSvc.createRecipe(r, email, file.getBytes());

            if (recipeId > 0) {
                String msgBody = EmailTemplate.constructRecipeCreated(
                            r.getName(),
                            "testing",//URLs.URL_DO_THUMBNAILS + "/" + thumbnailOpt.get(),
                            URLs.URL_HOME + "/#/recipe/user/" + recipeId);
                String subject = "New Recipe Created!";
                EmailDetails details = new EmailDetails(email, msgBody, subject);
                emailSvc.sendEmail(details);
    
                resp.setCode(HttpStatus.CREATED.value());
                resp.setMessage("Recipe created");
                JsonObject data = Json.createObjectBuilder().add("recipeId", recipeId).build();
                resp.setData(data);
                return ResponseEntity.status(HttpStatus.CREATED).body(resp.toJson().toString());
            } else {
                //s3Svc.delete(thumbnailOpt.get());
                resp.setCode(HttpStatus.BAD_REQUEST.value());
                resp.setMessage("Something went wrong when creating recipe!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp.toJson().toString());
            }

        } catch(IOException ex) {
            resp.setCode(HttpStatus.BAD_REQUEST.value());
            resp.setMessage("Something went wrong with thumbnail file!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp.toJson().toString());
        }
    }

    @GetMapping(path = "/recipe/{recipeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipeById(@PathVariable String recipeId) {
        Optional<Recipe> recipeOpt = myRecipeSvc.getRecipeById(recipeId);
        if (recipeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(recipeOpt.get().toJson().toString());
        }
    }

    @GetMapping(path = "/recipes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByEmail(@RequestParam String email) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByEmail(email);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v -> {
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @GetMapping(path = "/recipes/{name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByName(@PathVariable String name) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByName(name);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v -> {
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @GetMapping(path = "/recipes/category/{cat}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByCategory(@PathVariable String cat) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByCategory(cat);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v -> {
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @GetMapping(path = "/recipes/area/{area}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByArea(@PathVariable String area) {
        List<RecipeSummary> recipes = myRecipeSvc.getRecipesSummaryByArea(area);
        JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
        recipes.forEach(v -> {
            jArrayBuilder.add(v.toJson());
        });
        return ResponseEntity.ok(jArrayBuilder.build().toString());
    }

    @DeleteMapping(path = "/recipe/{recipeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteRecipeByRecipeId(@PathVariable String recipeId, @RequestBody String email) {
        Response resp = new Response();
        try {
            Optional<Recipe> recipeOpt = myRecipeSvc.getRecipeById(recipeId);

            if (recipeOpt.isEmpty()) {
                resp.setCode(HttpStatus.BAD_REQUEST.value());
                resp.setMessage("Something went wrong");
                return ResponseEntity.badRequest().body(resp.toJson().toString());
            }

            if (myRecipeSvc.deleteRecipeByRecipeId(recipeId)) {
                Recipe r = recipeOpt.get();
                //s3Svc.delete(r.getThumbnail());

                String msgBody = EmailTemplate.constructRecipeRemoved(
                            r.getName(),
                            URLs.URL_HOME + "/#/account/recipe/create");
                String subject = "Recipe removed!";
                EmailDetails details = new EmailDetails(email, msgBody, subject);
                emailSvc.sendEmail(details);

                resp.setCode(HttpStatus.OK.value());
                resp.setMessage("Deleted successfully");
                return ResponseEntity.ok().body(resp.toJson().toString());
            }
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        resp.setCode(HttpStatus.BAD_REQUEST.value());
        resp.setMessage("Something went wrong");
        return ResponseEntity.badRequest().body(resp.toJson().toString());
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> editRecipe(@RequestPart(name = "thumbnail", required = false) MultipartFile file,
            @RequestPart String name,
            @RequestPart String category,
            @RequestPart String area,
            @RequestPart String instructions,
            @RequestPart(name = "youtubeLink", required = false) String youtubeLink,
            @RequestPart String ingredients,
            @RequestPart String measurements,
            @RequestPart String email,
            @RequestPart String recipeId) {

        Optional<Recipe> recipeOpt = myRecipeSvc.getRecipeById(recipeId);

        Response resp = new Response();

        if (recipeOpt.isEmpty()) {
            resp.setCode(HttpStatus.NOT_FOUND.value());
            resp.setMessage("Oops! Recipe you are editing cannot be found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp.toJson().toString());
        }

        Recipe oldRecipe = recipeOpt.get();

        Recipe r = new Recipe();
        r.setRecipeId(oldRecipe.getRecipeId());
        r.setName(name);
        r.setCategory(category);
        r.setCountry(area);
        r.setInstructions(instructions);
        r.setYoutubeLink(youtubeLink);
        List<String> ingredientsList = Arrays.asList(ingredients.split(","));
        List<String> measurementsList = Arrays.asList(measurements.split(","));
        r.setIngredients(ingredientsList);
        r.setMeasurements(measurementsList);


        // if (file != null) {
        //     Optional<String> thumbnailOpt = s3Svc.upload(file, email);
        //     if (thumbnailOpt.isEmpty()) {
        //         return ResponseEntity.badRequest().build();
        //     }
        //     s3Svc.delete(oldRecipe.getThumbnail());
        //     r.setThumbnail(thumbnailOpt.get());
        // } else {
        //     r.setThumbnail(oldRecipe.getThumbnail());
        // }

        try {
            if (myRecipeSvc.editRecipe(r, email, file)) {

                String msgBody = EmailTemplate.constructRecipeEdited(
                            r.getName(),
                            "testing",//URLs.URL_DO_THUMBNAILS + "/" + r.getThumbnail(),
                            URLs.URL_HOME + "/#/recipe/user/" + r.getRecipeId()
                            );
                String subject = "Recipe Updated!";
                EmailDetails details = new EmailDetails(email, msgBody, subject);
                emailSvc.sendEmail(details);

                resp.setCode(HttpStatus.OK.value());
                resp.setMessage("Updated successfully");
                return ResponseEntity.ok().body(resp.toJson().toString());
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        resp.setCode(HttpStatus.BAD_REQUEST.value());
        resp.setMessage("Failed to update");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp.toJson().toString());
    }
}
