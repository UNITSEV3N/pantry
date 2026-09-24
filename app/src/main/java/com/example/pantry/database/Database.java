package com.example.pantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pantry.model.Ingredient;

public class Database extends SQLiteOpenHelper
{
    private static final String DB_NAME = "PantryDB";
    private static final int DB_VERSION = 1;

    // Ingredients
    private static final String T_INGREDIENT = "ingredient";
    private static final String I_ID = "id";
    private static final String I_NAME = "name";
    private static final String I_BRAND = "brand";
    private static final String I_DATE = "date_added";

    // Recipes
    private static final String T_RECIPE = "recipe";
    private static final String R_ID = "id";
    private static final String R_NAME = "name";
    private static final String R_INSTR = "instructions";
    private static final String R_DATE = "date_added";


    public Database(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + T_INGREDIENT + " ("
                + I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + I_NAME + " TEXT NOT NULL, "
                + I_BRAND + " TEXT, "
                + I_DATE + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + T_RECIPE + " ("
                + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + R_NAME + " TEXT NOT NULL, "
                + R_INSTR + " TEXT, "
                + R_DATE + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + T_INGREDIENT);
        onCreate(db);
    }

    // Ingredients Insertion
    public long insertIngredient(Ingredient item)
    {
        ContentValues values = new ContentValues();

        values.put(I_NAME, item.getName());
        values.put(I_BRAND, item.getBrand());
        values.put(I_DATE, item.getDate().getTime());

        return getWritableDatabase().insert(T_INGREDIENT, null, values);
    }
}
