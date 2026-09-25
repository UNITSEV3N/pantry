package com.example.pantry.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final String I_QUANTITY = "quantity";
    private static final String I_DATE = "date_added";
    private static final String I_EXPIRY = "expiry_date";

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
                + I_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + I_DATE + " INTEGER NOT NULL, "
                + I_EXPIRY + " INTEGER)");

        db.execSQL("CREATE TABLE " + T_RECIPE + " ("
                + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + R_NAME + " TEXT NOT NULL, "
                + R_INSTR + " TEXT, "
                + R_DATE + " INTEGER NOT NULL)");
    }

    public void clearAllTables(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + T_INGREDIENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        clearAllTables(db);
        onCreate(db);
    }

    // For testing purposes only
    public void resetDatabase()
    {
        SQLiteDatabase db = getWritableDatabase();
        clearAllTables(db);
        onCreate(db);
    }

    // Ingredients Insertion -----------------------------------------------------------------------

    public long insertIngredient(Ingredient item)
    {
        ContentValues values = new ContentValues();

        values.put(I_NAME, item.getName());
        values.put(I_BRAND, item.getBrand());
        values.put(I_QUANTITY, item.getQuantity());
        values.put(I_DATE, item.getDate().getTime());
        values.put(I_EXPIRY, item.getExpiryDate() != null ? item.getExpiryDate().getTime() : null);

        return getWritableDatabase().insert(T_INGREDIENT, null, values);
    }

    public List<Ingredient> getAllIngredients()
    {
        List<Ingredient> list = new ArrayList<>();

        Cursor cursor = getReadableDatabase().query(
                T_INGREDIENT,
                null,
                null, null, null, null,
                I_EXPIRY + " ASC");

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(I_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(I_NAME));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(I_BRAND));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(I_QUANTITY));
                long dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(I_DATE));

                int expiryColIndex = cursor.getColumnIndexOrThrow(I_EXPIRY);
                Date expiry = cursor.isNull(expiryColIndex) ? null : new Date(cursor.getLong(expiryColIndex));

                list.add(new Ingredient(id, name, brand, quantity, new Date(dateAdded), expiry));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}
