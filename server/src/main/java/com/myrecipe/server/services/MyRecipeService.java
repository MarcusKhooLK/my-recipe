package com.myrecipe.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myrecipe.server.models.Recipe;
import com.myrecipe.server.models.RecipeSummary;
import com.myrecipe.server.models.User;
import com.myrecipe.server.repository.AccountRepository;
import com.myrecipe.server.repository.MyRecipeRepository;

@Service
public class MyRecipeService {
    
    @Autowired
    private MyRecipeRepository myRecipeRepo;

    @Autowired
    private AccountRepository accRepo;

    @Transactional
    public int createRecipe(Recipe r, String email) {
        Optional<User> user = accRepo.findUserByEmail(email);

        if(user.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Integer recipeId = myRecipeRepo.insertRecipe(r, user.get().getUserId());

        if(recipeId < 0) {
            throw new IllegalArgumentException();
        }

        r.setRecipeId(recipeId.toString());

        boolean result = myRecipeRepo.insertIngredients(r.getIngredients(), r.getMeasurements(), recipeId);

        if(result) {
            return recipeId;
        }

        return -1;
    }

    public Optional<Recipe> getRecipeById(String recipedId) {
        try{
            Integer rId = Integer.parseInt(recipedId);
            return myRecipeRepo.getRecipeByRecipeId(rId);
        } catch(Exception e) {
            return Optional.empty();
        }  
    }

    public List<RecipeSummary> getRecipesSummaryByEmail(String email) {
        Optional<User> user = accRepo.findUserByEmail(email);

        if(user.isEmpty()) {
            return new ArrayList<RecipeSummary>();
        }

        return myRecipeRepo.getAllUserRecipesSummary(user.get().getUserId());
    }

    public List<RecipeSummary> getRecipesSummaryByName(String name) {
        return myRecipeRepo.getRecipesSummaryByName(name);
    }

    public List<RecipeSummary> getRecipesSummaryByCategory(String cat) {
        return myRecipeRepo.getRecipesSummaryByCategory(cat);
    }

    public List<RecipeSummary> getRecipesSummaryByArea(String area) {
        return myRecipeRepo.getRecipesSummaryByArea(area);
    }
}
