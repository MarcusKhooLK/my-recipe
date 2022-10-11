package com.myrecipe.server.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.myrecipe.server.models.Recipe;
import com.myrecipe.server.models.RecipeSummary;

@Repository
public class MyRecipeRepository {
    
    private static final String SQL_INSERT_RECIPE = "insert into recipe (name, category, country, instructions, thumbnail, youtubeLink, user_id) values (?, ?, ?, ?, ?, ?, ?);";
    private static final String SQL_INSERT_INGREDIENT = "insert into ingredient (name, measurement, recipe_id) values (?, ?, ?);";
    private static final String SQL_SELECT_RECIPE_BY_ID = "select * from recipe where recipe_id = ?;";
    private static final String SQL_SELECT_RECIPE_BY_USER_ID = "select * from recipe where user_id = ?;";
    private static final String SQL_SELECT_USERNAME_BY_RECIPE_ID = 
    """
    select u.username
    from user as u
    join recipe as r
    on u.user_id = r.user_id
    where r.recipe_id = ?;
    """;
    private static final String SQL_SELECT_RECIPE_BY_NAME = "select * from recipe where name like ?;";
    private static final String SQL_SELECT_RECIPE_BY_CATEGORY = "select * from recipe where category like ?;";
    private static final String SQL_SELECT_RECIPE_BY_AREA = "select * from recipe where country like ?;";
    private static final String SQL_DELETE_RECIPE_BY_ID = "delete from recipe where recipe_id = ?;";
    private static final String SQL_UPDATE_RECIPE = 
    """
    update recipe set name = ?, category = ?, country = ?, instructions = ?, thumbnail = ?, youtubeLink = ?
    where recipe_id = ? and user_id = ?;
    """;

    private static final String SQL_DELETE_INGREDIENTS_BY_RECIPEID = "delete from ingredient where recipe_id = ?;";
    private static final String SQL_SELECT_INGREDIENTS_BY_RECIPE_ID = "select * from ingredient where recipe_id = ?;";

    @Autowired
    private JdbcTemplate template;

    public int insertRecipe(Recipe r, int userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT_RECIPE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,r.getName());
            ps.setString(2,r.getCategory());
            ps.setString(3,r.getCountry());
            ps.setString(4,r.getInstructions());
            ps.setString(5,r.getThumbnail());
            ps.setString(6,r.getYoutubeLink());
            ps.setInt(7, userId);
            return ps;
        }, keyHolder);
        BigInteger bigInt = (BigInteger)keyHolder.getKey();
        if(bigInt == null) 
            return -1;
        return bigInt.intValue();
    }

    public boolean insertIngredients(List<String> ingredients, List<String> measurements, Integer recipeId) {
        if(ingredients.size() != measurements.size()) {
            throw new IllegalArgumentException();
        }

        List<Object[]> params = new ArrayList<>();
        for(int i = 0; i < ingredients.size(); i++) {
            Object[] row = new Object[3];
            row[0] = ingredients.get(i);
            row[1] = measurements.get(i);
            row[2] = recipeId;
            params.add(row);
        }

        int[] results = template.batchUpdate(SQL_INSERT_INGREDIENT, params);

        for(int i : results) {
            if(i <= 0) {
                throw new IllegalArgumentException();
            }
        }

        return true;
    }

    public Optional<Recipe> getRecipeByRecipeId(Integer recipeId) {
        final SqlRowSet recipeResult = template.queryForRowSet(SQL_SELECT_RECIPE_BY_ID, recipeId);
        
        if(recipeResult.next()) {
            Recipe r = Recipe.convert(recipeResult);

            final SqlRowSet username = template.queryForRowSet(SQL_SELECT_USERNAME_BY_RECIPE_ID, recipeId);
            if(username.next()) {
                r.setCreatedBy(username.getString("username"));
            } else {
                return Optional.empty();
            }

            final SqlRowSet ingredientResult = template.queryForRowSet(SQL_SELECT_INGREDIENTS_BY_RECIPE_ID, recipeId);
            List<String> ingredients = new ArrayList<String>();
            List<String> measurements = new ArrayList<String>();
            
            while(ingredientResult.next()) {
                ingredients.add(ingredientResult.getString("name"));
                measurements.add(ingredientResult.getString("measurement"));
            }

            r.setIngredients(ingredients);
            r.setMeasurements(measurements);

            return Optional.of(r);
        } else {
            return Optional.empty();
        }
    }

    public List<RecipeSummary> getAllUserRecipesSummary(Integer userId) {
        final SqlRowSet result = template.queryForRowSet(SQL_SELECT_RECIPE_BY_USER_ID, userId);

        List<RecipeSummary> recipes = new ArrayList<>();

        while(result.next()) {
            RecipeSummary r = RecipeSummary.convert(result);
            recipes.add(r);
        }

        return recipes;
    }

    public List<RecipeSummary> getRecipesSummaryByName(String name) {
        final SqlRowSet result = template.queryForRowSet(SQL_SELECT_RECIPE_BY_NAME, "%"+name+"%");
        List<RecipeSummary> recipes = new ArrayList<>();
        while(result.next()) {
            RecipeSummary r = RecipeSummary.convert(result);
            recipes.add(r);
        }
        return recipes;
    }

    public List<RecipeSummary> getRecipesSummaryByCategory(String cat) {
        final SqlRowSet result = template.queryForRowSet(SQL_SELECT_RECIPE_BY_CATEGORY, cat);
        List<RecipeSummary> recipes = new ArrayList<>();
        while(result.next()) {
            RecipeSummary r = RecipeSummary.convert(result);
            recipes.add(r);
        }
        return recipes;
    }

    public List<RecipeSummary> getRecipesSummaryByArea(String area) {
        final SqlRowSet result = template.queryForRowSet(SQL_SELECT_RECIPE_BY_AREA, area);
        List<RecipeSummary> recipes = new ArrayList<>();
        while(result.next()) {
            RecipeSummary r = RecipeSummary.convert(result);
            recipes.add(r);
        }
        return recipes;
    }

    public boolean deleteRecipeByRecipeId(Integer recipeId) {
        int result = template.update(SQL_DELETE_RECIPE_BY_ID, recipeId);
        return result > 0;
    }

    public boolean deleteIngredientsByRecipeId(Integer recipeId) {
        int result = template.update(SQL_DELETE_INGREDIENTS_BY_RECIPEID, recipeId);
        return result > 0;
    }

    public boolean editRecipe(Recipe recipe, Integer userId) {
        int result = template.update(SQL_UPDATE_RECIPE, 
        recipe.getName(), 
        recipe.getCategory(), 
        recipe.getCountry(),
        recipe.getInstructions(), 
        recipe.getThumbnail(), 
        recipe.getYoutubeLink(), 
        recipe.getRecipeId(), 
        userId);

        return result > 0;
    }

}
