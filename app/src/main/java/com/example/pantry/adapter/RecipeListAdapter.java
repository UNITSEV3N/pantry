package com.example.pantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantry.R;
import com.example.pantry.RecipeSection;
import com.example.pantry.model.Recipe;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public interface OnRecipeClickListener
    {
        void onRecipeClick(Recipe recipe);
    }

    private final List<RecipeSection> sections;
    private final OnRecipeClickListener listener;

    public RecipeListAdapter(List<RecipeSection> sections, OnRecipeClickListener listener)
    {
        this.sections = sections;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position)
    {
        return sections.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == RecipeSection.TYPE_HEADER)
        {
            return new HeaderViewHolder(inflater.inflate(R.layout.fragment_item_recipe_header, parent, false));
        }

        return new RecipeViewHolder(inflater.inflate(R.layout.fragment_item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        RecipeSection section = sections.get(position);

        if (holder instanceof HeaderViewHolder)
        {
            ((HeaderViewHolder) holder).headerText.setText(section.headerText);
        }
        else if (holder instanceof RecipeViewHolder)
        {
            RecipeViewHolder recipeHolder = (RecipeViewHolder) holder;

            recipeHolder.name.setText(section.recipe.getName());
            recipeHolder.subtitle.setText(section.subtitle);
            recipeHolder.subtitle.setTextColor(section.isReady ? 0xFF2E7D32 : 0xFFEF6C00); // green / amber

            recipeHolder.itemView.setOnClickListener(v ->
            {
                if (listener != null) listener.onRecipeClick(section.recipe);
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return sections.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        TextView headerText;

        HeaderViewHolder(View itemView)
        {
            super(itemView);
            headerText = itemView.findViewById(R.id.headerText);
        }
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder
    {
        TextView name, subtitle;

        RecipeViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.recipeName);
            subtitle = itemView.findViewById(R.id.recipeSubtitle);
        }
    }
}