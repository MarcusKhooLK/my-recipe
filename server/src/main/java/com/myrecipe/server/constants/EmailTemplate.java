package com.myrecipe.server.constants;

public class EmailTemplate {
    private static final String WELCOME_MESSAGE = """
        <div style="text-align:center;">
        <h1>Welcome to MyRecipe!</h1>
        <p>Thank you for joining MyRecipe, a platform to connect like-minded cooks like you! 
            A platform where you can search for simple recipes yet delicious dishes!</p>
        <p>Have your own recipe to share around the world? Fred not, create your own custom recipe and share your love around!</p>
        <p><a href="%s" style="
        background-color: orange;
        color: white;
        padding: 15px 25px;
        text-decoration: none;
        cursor: pointer;
        border: none;
        border-radius: 10px;
        margin: 10px;">Create your Recipe</a></p>
        </div>
        """;
    
    private static final String RECIPE_CREATE_MESSAGE = """
        <div style="text-align:center;">
        <h1>Nicely done!</h1>
        <p>You have created your own %s recipe and is now available for cooks around the world to try it!</p>
        <p><img src="%s"></p>
        <p><a href="%s" style="
        background-color: orange;
        color: white;
        padding: 15px 25px;
        text-decoration: none;
        cursor: pointer;
        border: none;
        border-radius: 10px;
        margin: 10px;">View Recipe</a></p>
        </div>
        """;

    private static final String RECIPE_REMOVED_MESSAGE = """
        <div style="text-align:center;">
        <h1>Recipe Removed</h1>
        <p>Say goodbye to your %s recipe!</p>
        <p>Feel free to add more into your collection!</p>
        <p><a href="%s" style="
        background-color: orange;
        color: white;
        padding: 15px 25px;
        text-decoration: none;
        cursor: pointer;
        border: none;
        border-radius: 10px;
        margin: 10px;">Create Recipe</a></p>
        </div>
        """;

    private static final String RECIPE_EDIT_MESSAGE = """
        <div style="text-align:center;">
        <h1>Your recipe was updated</h1>
        <p>You have updated your <b>%s</b> recipe!</p>
        <p><img src="%s"></p>
        <p><a href="%s" style="
        background-color: orange;
        color: white;
        padding: 15px 25px;
        text-decoration: none;
        cursor: pointer;
        border: none;
        border-radius: 10px;
        margin: 10px;">View Recipe</a></p>
        </div>
        """;
    
    public static String constructWelcome(final String url) {
        return WELCOME_MESSAGE.formatted(url);
    }

    public static String constructRecipeCreated(final String recipeName, final String thumbnailUrl, final String recipeUrl) {
        return RECIPE_CREATE_MESSAGE.formatted(recipeName, thumbnailUrl, recipeUrl);
    }

    public static String constructRecipeRemoved(final String recipeName, final String url) {
        return RECIPE_REMOVED_MESSAGE.formatted(recipeName, url);
    }

    public static String constructRecipeEdited(final String recipeName, final String thumbnailUrl, final String recipeUrl) {
        return RECIPE_EDIT_MESSAGE.formatted(recipeName, thumbnailUrl, recipeUrl);
    }
}
