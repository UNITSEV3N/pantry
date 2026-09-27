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
import com.example.pantry.model.Recipe;
import com.example.pantry.model.RecipeIngredient;

public class Database extends SQLiteOpenHelper
{
    private static final String DB_NAME = "PantryDB";
    private static final int DB_VERSION = 2;

    // Ingredients
    private static final String T_INGREDIENT = "ingredient";
    private static final String I_ID = "id";
    private static final String I_NAME = "name";
    private static final String I_BRAND = "brand";
    private static final String I_QUANTITY = "quantity";
    private static final String I_UNIT = "unit";
    private static final String I_DATE = "date_added";
    private static final String I_EXPIRY = "expiry_date";

    // Recipes
    private static final String T_RECIPE = "recipe";
    private static final String R_ID = "id";
    private static final String R_NAME = "name";
    private static final String R_INSTR = "instructions";
    private static final String R_DATE = "date_added";

    // Recipe ingredients (one row per ingredient a recipe needs)
    private static final String T_RECIPE_INGREDIENT = "recipe_ingredient";
    private static final String RI_ID = "id";
    private static final String RI_RECIPE_ID = "recipe_id";
    private static final String RI_NAME = "name";
    private static final String RI_QUANTITY = "quantity";
    private static final String RI_UNIT = "unit";

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
                + I_UNIT + " TEXT, "
                + I_DATE + " INTEGER NOT NULL, "
                + I_EXPIRY + " INTEGER)");

        db.execSQL("CREATE TABLE " + T_RECIPE + " ("
                + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + R_NAME + " TEXT NOT NULL, "
                + R_INSTR + " TEXT, "
                + R_DATE + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + T_RECIPE_INGREDIENT + " ("
                + RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RI_RECIPE_ID + " INTEGER NOT NULL, "
                + RI_NAME + " TEXT NOT NULL, "
                + RI_QUANTITY + " REAL NOT NULL DEFAULT 0, "
                + RI_UNIT + " TEXT)");

        seedRecipes(db);
        seedIngredients(db);
    }

    public void clearAllTables(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE_INGREDIENT);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + T_INGREDIENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        clearAllTables(db);
        onCreate(db);
    }

    // For testing purposes only. Note: this also re-seeds the 20 default recipes,
    // since onCreate() runs again — recipes are treated as app content, not user data.
    public void resetDatabase()
    {
        SQLiteDatabase db = getWritableDatabase();
        clearAllTables(db);
        onCreate(db);

        // Re-seed ingredients
        seedRecipes(db);
        seedIngredients(db);
    }

    // Clear only ingredients
    public void clearPantry()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + T_INGREDIENT);
        db.execSQL("CREATE TABLE " + T_INGREDIENT + " ("
                + I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + I_NAME + " TEXT NOT NULL, "
                + I_BRAND + " TEXT, "
                + I_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + I_UNIT + " TEXT, "
                + I_DATE + " INTEGER NOT NULL, "
                + I_EXPIRY + " INTEGER)");
    }

    // Ingredients ----------------------------------------------------------------------------

    public long insertIngredient(Ingredient item)
    {
        ContentValues values = new ContentValues();

        values.put(I_NAME, item.getName());
        values.put(I_BRAND, item.getBrand());
        values.put(I_QUANTITY, item.getQuantity());
        values.put(I_UNIT, item.getUnit());
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

                int unitColIndex = cursor.getColumnIndexOrThrow(I_UNIT);
                String unit = cursor.isNull(unitColIndex) ? null : cursor.getString(unitColIndex);

                long dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(I_DATE));

                int expiryColIndex = cursor.getColumnIndexOrThrow(I_EXPIRY);
                Date expiry = cursor.isNull(expiryColIndex) ? null : new Date(cursor.getLong(expiryColIndex));

                list.add(new Ingredient(id, name, brand, quantity, unit, new Date(dateAdded), expiry));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public int deleteIngredient(int id)
    {
        return getWritableDatabase().delete(T_INGREDIENT, I_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int updateIngredient(Ingredient item)
    {
        ContentValues values = new ContentValues();

        values.put(I_NAME, item.getName());
        values.put(I_BRAND, item.getBrand());
        values.put(I_QUANTITY, item.getQuantity());
        values.put(I_UNIT, item.getUnit());
        values.put(I_EXPIRY, item.getExpiryDate() != null ? item.getExpiryDate().getTime() : null);

        return getWritableDatabase().update(T_INGREDIENT, values, I_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    private void insertSeedIngredient(SQLiteDatabase db, String name, String brand, int quantity, String unit, Integer expiryDaysFromNow)
    {
        ContentValues values = new ContentValues();
        values.put(I_NAME, name);
        values.put(I_BRAND, brand);
        values.put(I_QUANTITY, quantity);
        values.put(I_UNIT, unit);
        values.put(I_DATE, System.currentTimeMillis());

        if (expiryDaysFromNow != null)
        {
            long expiryMillis = System.currentTimeMillis() + (expiryDaysFromNow * 24L * 60 * 60 * 1000);
            values.put(I_EXPIRY, expiryMillis);
        }
        else
        {
            values.putNull(I_EXPIRY);
        }

        db.insert(T_INGREDIENT, null, values);
    }

    private void seedIngredients(SQLiteDatabase db)
    {
        insertSeedIngredient(db, "Eggs", "Farmhouse", 6, "", null);
        insertSeedIngredient(db, "Butter", "Lurpak", 100, "g", 30);
        insertSeedIngredient(db, "Salt", "Generic", 50, "g", null);
        insertSeedIngredient(db, "Milk", "Clover", 500, "ml", 5);
        insertSeedIngredient(db, "Flour", "Snowflake", 300, "g", 60);
        insertSeedIngredient(db, "Sugar", "Huletts", 100, "g", null);
        insertSeedIngredient(db, "Bread", "Albany", 4, "", 4);
        insertSeedIngredient(db, "Cheese", "Gouda", 100, "g", 14);
        insertSeedIngredient(db, "Pasta", "Fatti's", 300, "g", 180);
        insertSeedIngredient(db, "Tomatoes", "Fresh", 6, "", 6);
        insertSeedIngredient(db, "Garlic", "Fresh", 10, "", 20);
        insertSeedIngredient(db, "Olive Oil", "Extra Virgin", 100, "ml", 200);
        insertSeedIngredient(db, "Rice", "Tastic", 500, "g", 365);
        insertSeedIngredient(db, "Onions", "Fresh", 5, "", 14);
        insertSeedIngredient(db, "Soy Sauce", "Kikkoman", 100, "ml", 365);
        insertSeedIngredient(db, "Potato", "Fresh", 6, "", 21);
        insertSeedIngredient(db, "Banana", "Fresh", 3, "", 5);
        insertSeedIngredient(db, "Lettuce", "Fresh", 1, "", 5);
        insertSeedIngredient(db, "Cucumber", "Fresh", 1, "", 7);
    }

    // Recipes ----------------------------------------------------------------------------

    public List<Recipe> getAllRecipesWithIngredients()
    {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor recipeCursor = db.query(T_RECIPE, null, null, null, null, null, R_NAME + " ASC");

        if (recipeCursor.moveToFirst())
        {
            do
            {
                int id = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(R_ID));
                String name = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(R_NAME));
                String instructions = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(R_INSTR));
                long dateAdded = recipeCursor.getLong(recipeCursor.getColumnIndexOrThrow(R_DATE));

                List<RecipeIngredient> ingredients = new ArrayList<>();
                Cursor ingredientCursor = db.query(T_RECIPE_INGREDIENT, null,
                        RI_RECIPE_ID + " = ?", new String[]{String.valueOf(id)},
                        null, null, RI_NAME + " ASC");

                if (ingredientCursor.moveToFirst())
                {
                    do
                    {
                        String ingredientName = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(RI_NAME));
                        double quantity = ingredientCursor.getDouble(ingredientCursor.getColumnIndexOrThrow(RI_QUANTITY));
                        String unit = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow(RI_UNIT));

                        ingredients.add(new RecipeIngredient(ingredientName, quantity, unit));
                    }
                    while (ingredientCursor.moveToNext());
                }
                ingredientCursor.close();

                recipes.add(new Recipe(id, name, instructions, new Date(dateAdded), ingredients));
            }
            while (recipeCursor.moveToNext());
        }

        recipeCursor.close();
        return recipes;
    }

    private void insertSeedRecipe(SQLiteDatabase db, String name, String instructions, RecipeIngredient... ingredients)
    {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(R_NAME, name);
        recipeValues.put(R_INSTR, instructions);
        recipeValues.put(R_DATE, System.currentTimeMillis());

        long recipeId = db.insert(T_RECIPE, null, recipeValues);

        for (RecipeIngredient ingredient : ingredients)
        {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(RI_RECIPE_ID, recipeId);
            ingredientValues.put(RI_NAME, ingredient.getName());
            ingredientValues.put(RI_QUANTITY, ingredient.getQuantity());
            ingredientValues.put(RI_UNIT, ingredient.getUnit());

            db.insert(T_RECIPE_INGREDIENT, null, ingredientValues);
        }
    }

    private void seedRecipes(SQLiteDatabase db)
    {
        insertSeedRecipe(db, "Scrambled Eggs",
                "Whisk eggs with a pinch of salt. Melt butter in a pan over low heat, add eggs, and stir gently until just set. Add a splash of milk for creaminess.",
                new RecipeIngredient("Eggs", 2, ""),
                new RecipeIngredient("Butter", 10, "g"),
                new RecipeIngredient("Salt", 1, "g"),
                new RecipeIngredient("Milk", 30, "ml"));

        insertSeedRecipe(db, "Pancakes",
                "Mix flour and sugar. Whisk in eggs and milk until smooth. Melt butter in a pan and cook spoonfuls of batter until bubbles form, then flip.",
                new RecipeIngredient("Flour", 200, "g"),
                new RecipeIngredient("Eggs", 2, ""),
                new RecipeIngredient("Milk", 300, "ml"),
                new RecipeIngredient("Sugar", 20, "g"),
                new RecipeIngredient("Butter", 20, "g"));

        insertSeedRecipe(db, "Grilled Cheese Sandwich",
                "Butter one side of each bread slice. Place cheese between the unbuttered sides and grill in a pan until golden on both sides and the cheese melts.",
                new RecipeIngredient("Bread", 2, ""),
                new RecipeIngredient("Cheese", 50, "g"),
                new RecipeIngredient("Butter", 10, "g"));

        insertSeedRecipe(db, "Tomato Pasta",
                "Cook pasta until al dente. Sauté garlic in olive oil, add chopped tomatoes and salt, simmer until saucy, then toss with the pasta.",
                new RecipeIngredient("Pasta", 200, "g"),
                new RecipeIngredient("Tomato", 3, ""),
                new RecipeIngredient("Garlic", 2, ""),
                new RecipeIngredient("Olive Oil", 20, "ml"),
                new RecipeIngredient("Salt", 1, "g"));

        insertSeedRecipe(db, "Fried Rice",
                "Scramble eggs and set aside. Fry chopped onion and garlic, add cooked rice and soy sauce, stir in the eggs, and toss until heated through.",
                new RecipeIngredient("Rice", 300, "g"),
                new RecipeIngredient("Eggs", 2, ""),
                new RecipeIngredient("Onion", 1, ""),
                new RecipeIngredient("Soy Sauce", 20, "ml"),
                new RecipeIngredient("Garlic", 1, ""));

        insertSeedRecipe(db, "Cheese Omelette",
                "Whisk eggs with salt. Melt butter in a pan, pour in the eggs, sprinkle cheese over one half once set, then fold and serve.",
                new RecipeIngredient("Eggs", 3, ""),
                new RecipeIngredient("Cheese", 40, "g"),
                new RecipeIngredient("Butter", 10, "g"),
                new RecipeIngredient("Salt", 1, "g"));

        insertSeedRecipe(db, "Garlic Bread",
                "Mix softened butter with crushed garlic. Spread generously over sliced bread and toast or grill until golden.",
                new RecipeIngredient("Bread", 1, ""),
                new RecipeIngredient("Butter", 30, "g"),
                new RecipeIngredient("Garlic", 3, ""));

        insertSeedRecipe(db, "Chicken Stir Fry",
                "Slice chicken and stir-fry in olive oil until browned. Add onion and garlic, cook until soft, then stir in soy sauce and simmer briefly.",
                new RecipeIngredient("Chicken", 300, "g"),
                new RecipeIngredient("Onion", 1, ""),
                new RecipeIngredient("Garlic", 2, ""),
                new RecipeIngredient("Soy Sauce", 30, "ml"),
                new RecipeIngredient("Olive Oil", 15, "ml"));

        insertSeedRecipe(db, "Vegetable Soup",
                "Sauté onion and garlic, add chopped carrot and potato with water to cover, season with salt, and simmer until the vegetables are tender.",
                new RecipeIngredient("Onion", 1, ""),
                new RecipeIngredient("Carrot", 2, ""),
                new RecipeIngredient("Potato", 2, ""),
                new RecipeIngredient("Garlic", 1, ""),
                new RecipeIngredient("Salt", 2, "g"));

        insertSeedRecipe(db, "Banana Smoothie",
                "Blend banana, milk, and sugar together until smooth. Serve chilled.",
                new RecipeIngredient("Banana", 2, ""),
                new RecipeIngredient("Milk", 200, "ml"),
                new RecipeIngredient("Sugar", 10, "g"));

        insertSeedRecipe(db, "Rice and Beans",
                "Cook rice separately. Sauté onion and garlic, stir in beans and a little water, simmer, then serve over the rice.",
                new RecipeIngredient("Rice", 200, "g"),
                new RecipeIngredient("Beans", 150, "g"),
                new RecipeIngredient("Onion", 1, ""),
                new RecipeIngredient("Garlic", 1, ""));

        insertSeedRecipe(db, "Buttered Toast",
                "Toast the bread and spread with butter while warm.",
                new RecipeIngredient("Bread", 2, ""),
                new RecipeIngredient("Butter", 15, "g"));

        insertSeedRecipe(db, "Simple Salad",
                "Tear the lettuce, slice the tomato and cucumber, combine in a bowl, and drizzle with olive oil.",
                new RecipeIngredient("Lettuce", 1, ""),
                new RecipeIngredient("Tomato", 2, ""),
                new RecipeIngredient("Cucumber", 1, ""),
                new RecipeIngredient("Olive Oil", 10, "ml"));

        insertSeedRecipe(db, "Tomato Soup",
                "Sauté onion and garlic in butter, add chopped tomatoes and salt, simmer until soft, then blend until smooth.",
                new RecipeIngredient("Tomato", 5, ""),
                new RecipeIngredient("Onion", 1, ""),
                new RecipeIngredient("Garlic", 1, ""),
                new RecipeIngredient("Butter", 15, "g"),
                new RecipeIngredient("Salt", 2, "g"));

        insertSeedRecipe(db, "Mashed Potatoes",
                "Boil the potatoes until tender, drain, then mash with butter, milk, and salt until smooth.",
                new RecipeIngredient("Potato", 4, ""),
                new RecipeIngredient("Butter", 20, "g"),
                new RecipeIngredient("Milk", 50, "ml"),
                new RecipeIngredient("Salt", 2, "g"));

        insertSeedRecipe(db, "Spaghetti Aglio e Olio",
                "Cook pasta until al dente. Gently fry sliced garlic in olive oil until golden, season with salt, then toss through the pasta.",
                new RecipeIngredient("Pasta", 200, "g"),
                new RecipeIngredient("Garlic", 4, ""),
                new RecipeIngredient("Olive Oil", 40, "ml"),
                new RecipeIngredient("Salt", 2, "g"));

        insertSeedRecipe(db, "Egg Fried Noodles",
                "Cook noodles and set aside. Scramble eggs in a hot pan, add chopped onion and the noodles, then stir in soy sauce until combined.",
                new RecipeIngredient("Noodles", 200, "g"),
                new RecipeIngredient("Eggs", 2, ""),
                new RecipeIngredient("Soy Sauce", 20, "ml"),
                new RecipeIngredient("Onion", 1, ""));

        insertSeedRecipe(db, "Chicken Curry",
                "Sauté onion and garlic, add chicken pieces and curry powder, cook until browned, then stir in milk and simmer until the chicken is cooked through.",
                new RecipeIngredient("Chicken", 400, "g"),
                new RecipeIngredient("Onion", 2, ""),
                new RecipeIngredient("Garlic", 2, ""),
                new RecipeIngredient("Curry Powder", 10, "g"),
                new RecipeIngredient("Milk", 100, "ml"));

        insertSeedRecipe(db, "Pasta with Butter and Cheese",
                "Cook pasta until al dente, drain, then toss immediately with butter and grated cheese and a pinch of salt until melted and coated.",
                new RecipeIngredient("Pasta", 200, "g"),
                new RecipeIngredient("Butter", 20, "g"),
                new RecipeIngredient("Cheese", 40, "g"),
                new RecipeIngredient("Salt", 1, "g"));

        insertSeedRecipe(db, "Onion Soup",
                "Slowly cook sliced onion and garlic in butter until deeply caramelized, season with salt, then add water and simmer.",
                new RecipeIngredient("Onion", 4, ""),
                new RecipeIngredient("Butter", 20, "g"),
                new RecipeIngredient("Garlic", 1, ""),
                new RecipeIngredient("Salt", 2, "g"));
    }
}