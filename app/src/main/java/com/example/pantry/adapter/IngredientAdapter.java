package com.example.pantry.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantry.R;
import com.example.pantry.model.Ingredient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder>
{
    private static final int EXPIRY_WARNING_DAYS = 3;

    public interface OnItemClickListener
    {
        void onItemClick(Ingredient item);
    }

    private final List<Ingredient> items;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private final OnItemClickListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public IngredientAdapter(List<Ingredient> items, OnItemClickListener listener)
    {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Ingredient item = items.get(position);

        holder.name.setText(item.getName());

        if (TextUtils.isEmpty(item.getBrand()))
        {
            holder.brand.setVisibility(View.GONE);
        }
        else
        {
            holder.brand.setVisibility(View.VISIBLE);
            holder.brand.setText(item.getBrand());
        }

        String unit = item.getUnit();
        String quantityText = TextUtils.isEmpty(unit)
                ? "x" + item.getQuantity()
                : "x" + item.getQuantity() + " " + unit;
        holder.quantity.setText(quantityText);

        if (item.getDate() == null)
        {
            holder.dateAdded.setVisibility(View.GONE);
        }
        else
        {
            holder.dateAdded.setVisibility(View.VISIBLE);
            holder.dateAdded.setText(holder.itemView.getContext()
                    .getString(R.string.date_added_format, dateFormat.format(item.getDate())));
        }

        if (item.getExpiryDate() == null)
        {
            holder.expiry.setVisibility(View.GONE);
        }
        else
        {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText(holder.itemView.getContext()
                    .getString(R.string.expiry_format, dateFormat.format(item.getExpiryDate())));
            holder.expiry.setTextColor(getExpiryColor(item.getExpiryDate()));
        }

        holder.itemView.setActivated(position == selectedPosition);

        holder.itemView.setOnClickListener(v ->
        {
            int clickedPosition = holder.getBindingAdapterPosition();
            if (clickedPosition == RecyclerView.NO_POSITION) return;

            int previousSelected = selectedPosition;
            selectedPosition = (selectedPosition == clickedPosition) ? RecyclerView.NO_POSITION : clickedPosition;

            if (previousSelected != RecyclerView.NO_POSITION) notifyItemChanged(previousSelected);
            if (selectedPosition != RecyclerView.NO_POSITION) notifyItemChanged(selectedPosition);

            if (listener != null)
            {
                listener.onItemClick(selectedPosition == RecyclerView.NO_POSITION ? null : items.get(selectedPosition));
            }
        });
    }

    private int getExpiryColor(Date expiryDate)
    {
        long daysUntil = daysBetween(new Date(), expiryDate);

        if (daysUntil <= 0)
        {
            return Color.RED;
        }
        else if (daysUntil <= EXPIRY_WARNING_DAYS)
        {
            return Color.parseColor("#FFC107"); // amber/yellow
        }
        else
        {
            return Color.BLACK;
        }
    }

    private long daysBetween(Date from, Date to)
    {
        Calendar cFrom = Calendar.getInstance();
        cFrom.setTime(from);
        clearTime(cFrom);

        Calendar cTo = Calendar.getInstance();
        cTo.setTime(to);
        clearTime(cTo);

        long diff = cTo.getTimeInMillis() - cFrom.getTimeInMillis();
        return diff / (24 * 60 * 60 * 1000);
    }

    private void clearTime(Calendar c)
    {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void setItems(List<Ingredient> newItems)
    {
        items.clear();
        items.addAll(newItems);
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public void clearSelection()
    {
        int previousSelected = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        if (previousSelected != RecyclerView.NO_POSITION) notifyItemChanged(previousSelected);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name, brand, quantity, dateAdded, expiry;

        ViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.ingredientName);
            brand = itemView.findViewById(R.id.ingredientBrand);
            quantity = itemView.findViewById(R.id.ingredientQuantity);
            dateAdded = itemView.findViewById(R.id.ingredientDateAdded);
            expiry = itemView.findViewById(R.id.ingredientExpiry);
        }
    }
}