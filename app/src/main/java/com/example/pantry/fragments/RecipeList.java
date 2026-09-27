package com.example.pantry.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pantry.R;
import com.example.pantry.adapter.RecipeListAdapter;
import com.example.pantry.database.Database;
import com.example.pantry.RecipeSection;
import com.example.pantry.util.RecipeMatcher;
import com.example.pantry.model.Ingredient;
import com.example.pantry.model.Recipe;
import com.example.pantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeList extends Fragment
{
    private Database database;
    private RecyclerView recyclerView;
    private View emptyState;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        database = new Database(requireContext());
        getParentFragmentManager().setFragmentResultListener("pantry_changed", this,
                (requestKey, bundle) -> refreshRecipes());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        recyclerView = view.findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        emptyState = view.findViewById(R.id.emptyState);

        refreshRecipes();

        return view;
    }

    private void refreshRecipes()
    {
        if (recyclerView == null || emptyState == null)
        {
            return;
        }

        List<Recipe> allRecipes = database.getAllRecipesWithIngredients();
        List<Ingredient> pantry = database.getAllIngredients();

        List<Recipe> ready = RecipeMatcher.getStrictSuggestions(allRecipes, pantry);
        List<RecipeMatcher.MatchResult> almostThere = RecipeMatcher.getAlmostThere(allRecipes, pantry);

        List<RecipeSection> sections = new ArrayList<>();

        if (!ready.isEmpty())
        {
            sections.add(RecipeSection.header("Ready to Cook (" + ready.size() + ")"));
            for (Recipe recipe : ready)
            {
                sections.add(RecipeSection.recipe(recipe, "Ready to cook", true));
            }
        }

        if (!almostThere.isEmpty())
        {
            sections.add(RecipeSection.header("Almost There"));
            for (RecipeMatcher.MatchResult result : almostThere)
            {
                String missingName = result.getMissing().get(0).getName();
                sections.add(RecipeSection.recipe(result.getRecipe(), "Missing: " + missingName, false));
            }
        }

        boolean isEmpty = sections.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        recyclerView.setAdapter(new RecipeListAdapter(sections, this::showRecipeDetails));
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        recyclerView = null;
        emptyState = null;
    }

    private void showRecipeDetails(Recipe recipe)
    {
        StringBuilder message = new StringBuilder();

        for (RecipeIngredient ingredient : recipe.getIngredients())
        {
            String unit = ingredient.getUnit();
            String qty = ingredient.getQuantity() == (long) ingredient.getQuantity()
                    ? String.valueOf((long) ingredient.getQuantity())
                    : String.valueOf(ingredient.getQuantity());

            message.append("• ").append(qty);
            if (!TextUtils.isEmpty(unit)) message.append(" ").append(unit);
            message.append(" ").append(ingredient.getName()).append("\n");
        }

        message.append("\n").append(recipe.getInstructions());

        new AlertDialog.Builder(requireContext())
                .setTitle(recipe.getName())
                .setMessage(message.toString())
                .setPositiveButton("Close", null)
                .show();
    }
}