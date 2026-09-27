package com.example.pantry.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipe
{
    private int id;
    private String name;
    private String instructions;
    private Date date;
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe()
    {

    }

    public Recipe(int id, String name, String instructions, Date date, List<RecipeIngredient> ingredients)
    {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.date = date;
        this.ingredients = ingredients;
    }

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getInstructions()
    {
        return instructions;
    }
    public void setInstructions(String instructions)
    {
        this.instructions = instructions;
    }

    public Date getDate()
    {
        return date;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }

    public List<RecipeIngredient> getIngredients()
    {
        return ingredients;
    }
    public void setIngredients(List<RecipeIngredient> ingredients)
    {
        this.ingredients = ingredients;
    }
}