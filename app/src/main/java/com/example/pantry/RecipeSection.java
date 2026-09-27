package com.example.pantry;

import com.example.pantry.model.Recipe;

public class RecipeSection
{
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_RECIPE = 1;

    public final int type;
    public final String headerText;
    public final Recipe recipe;
    public final String subtitle;
    public final boolean isReady;

    private RecipeSection(int type, String headerText, Recipe recipe, String subtitle, boolean isReady)
    {
        this.type = type;
        this.headerText = headerText;
        this.recipe = recipe;
        this.subtitle = subtitle;
        this.isReady = isReady;
    }

    public static RecipeSection header(String text)
    {
        return new RecipeSection(TYPE_HEADER, text, null, null, false);
    }

    public static RecipeSection recipe(Recipe recipe, String subtitle, boolean isReady)
    {
        return new RecipeSection(TYPE_RECIPE, null, recipe, subtitle, isReady);
    }
}