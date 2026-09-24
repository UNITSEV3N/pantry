package com.example.pantry.model;

import java.util.Date;

public class Ingredient
{
    private int id;
    private String name;
    private String brand;
    private Date date;

    public Ingredient()
    {

    }

    public Ingredient(int id, String name, String brand, Date date_added)
    {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.date = date_added;
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

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}
