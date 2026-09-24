package com.example.pantry.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipe
{
    private int id;
    private String name;
    private String instructions;
    private Date date_added;

    public Recipe(int id, String name, String instructions, Date date_added)
    {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.date_added = date_added;
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

    public Date getDate_added()
    {
        return date_added;
    }

    public void setDate_added(Date date_added)
    {
        this.date_added = date_added;
    }
}