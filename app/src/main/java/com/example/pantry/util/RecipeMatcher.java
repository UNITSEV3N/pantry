package com.example.pantry.util;

import com.example.pantry.model.Ingredient;
import com.example.pantry.model.Recipe;
import com.example.pantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeMatcher
{
    public static class MatchResult
    {
        private final Recipe recipe;
        private final List<RecipeIngredient> missing;

        public MatchResult(Recipe recipe, List<RecipeIngredient> missing)
        {
            this.recipe = recipe;
            this.missing = missing;
        }

        public Recipe getRecipe() { return recipe; }
        public List<RecipeIngredient> getMissing() { return missing; }
        public boolean isFullMatch() { return missing.isEmpty(); }
        public int missingCount() { return missing.size(); }
    }

    public static List<MatchResult> matchAll(List<Recipe> recipes, List<Ingredient> pantry)
    {
        List<MatchResult> results = new ArrayList<>();

        for (Recipe recipe : recipes)
        {
            List<RecipeIngredient> missing = new ArrayList<>();

            for (RecipeIngredient required : recipe.getIngredients())
            {
                if (!isAvailable(required, pantry))
                {
                    missing.add(required);
                }
            }

            results.add(new MatchResult(recipe, missing));
        }

        return results;
    }

    public static List<Recipe> getStrictSuggestions(List<Recipe> recipes, List<Ingredient> pantry)
    {
        List<Recipe> suggestions = new ArrayList<>();

        for (MatchResult result : matchAll(recipes, pantry))
        {
            if (result.isFullMatch())
            {
                suggestions.add(result.getRecipe());
            }
        }

        return suggestions;
    }

    public static List<MatchResult> getAlmostThere(List<Recipe> recipes, List<Ingredient> pantry)
    {
        List<MatchResult> almost = new ArrayList<>();

        for (MatchResult result : matchAll(recipes, pantry))
        {
            if (result.missingCount() == 1)
            {
                almost.add(result);
            }
        }

        return almost;
    }

    private static boolean isAvailable(RecipeIngredient required, List<Ingredient> pantry)
    {
        String normalizedRequired = NameMatching.normalize(required.getName());

        boolean found = false;
        double totalAvailable = 0;

        for (Ingredient stock : pantry)
        {
            if (NameMatching.normalize(stock.getName()).equals(normalizedRequired))
            {
                found = true;
                totalAvailable += UnitConverter.convert(stock.getQuantity(), stock.getUnit(), required.getUnit());
            }
        }

        if (!found)
        {
            return false;
        }

        return totalAvailable >= required.getQuantity() - 0.0001;
    }
}
