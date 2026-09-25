package com.example.pantry.model;

import java.util.Date;

public class Ingredient
{
    private int id;
    private String name;
    private String brand;
    private int quantity;
    private Date date;
    private Date expiryDate;

    public Ingredient()
    {

    }

    public Ingredient(int id, String name, String brand, int quantity, Date date_added, Date expiryDate)
    {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
        this.date = date_added;
        this.expiryDate = expiryDate;
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

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public Date getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }
}
