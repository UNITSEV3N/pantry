package com.example.pantry.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pantry.R;
import com.example.pantry.adapter.IngredientAdapter;
import com.example.pantry.database.Database;
import com.example.pantry.model.Ingredient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PantryList extends Fragment
{
    private Database database;
    private IngredientAdapter adapter;

    private final SimpleDateFormat displayFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private Date selectedExpiryDate;
    private Ingredient selectedIngredient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        database = new Database(requireContext());
        // database.resetDatabase();

        View view = inflater.inflate(R.layout.fragment_pantry_list, container, false);
        view.findViewById(R.id.addFloatingButton).setOnClickListener(v -> showIngredientDialog(null));
        view.findViewById(R.id.editFloatingButton).setOnClickListener(v -> editSelectedIngredient());
        view.findViewById(R.id.removeFloatingButton).setOnClickListener(v -> removeSelectedIngredient());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new IngredientAdapter(database.getAllIngredients(), item -> selectedIngredient = item);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void editSelectedIngredient()
    {
        if (selectedIngredient == null)
        {
            Toast.makeText(requireContext(), "Select an ingredient first", Toast.LENGTH_SHORT).show();
            return;
        }

        showIngredientDialog(selectedIngredient);
    }

    private void removeSelectedIngredient()
    {
        if (selectedIngredient == null)
        {
            Toast.makeText(requireContext(), "Select an ingredient first", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = selectedIngredient.getName();
        int deletedRows = database.deleteIngredient(selectedIngredient.getId());

        if (deletedRows == 0)
        {
            Toast.makeText(requireContext(), "Failed to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), name + " removed", Toast.LENGTH_SHORT).show();
        selectedIngredient = null;
        refreshList();
    }

    private void showIngredientDialog(Ingredient existing)
    {
        boolean isEdit = existing != null;
        selectedExpiryDate = isEdit ? existing.getExpiryDate() : null;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_ingredient_dialog, null);

        EditText Name = dialogView.findViewById(R.id.IngredientName);
        EditText Brand = dialogView.findViewById(R.id.IngredientBrand);
        EditText Quantity = dialogView.findViewById(R.id.IngredientQuantity);
        EditText Unit = dialogView.findViewById(R.id.IngredientUnit);
        EditText Expiry = dialogView.findViewById(R.id.IngredientExpiry);

        if (isEdit)
        {
            Name.setText(existing.getName());
            Brand.setText(existing.getBrand());
            Quantity.setText(String.valueOf(existing.getQuantity()));
            Unit.setText(existing.getUnit());
            if (existing.getExpiryDate() != null)
            {
                Expiry.setText(displayFormat.format(existing.getExpiryDate()));
            }
        }

        Expiry.setOnClickListener(v -> showDatePicker(Expiry));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "Edit ingredient" : "Add ingredient")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Save" : "Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                {
                    String name = Name.getText().toString().trim();
                    String brand = Brand.getText().toString().trim();
                    String quantityStr = Quantity.getText().toString().trim();
                    String unit = Unit.getText().toString().trim();

                    if (TextUtils.isEmpty(name))
                    {
                        Name.setError("Required");
                        return;
                    }

                    int quantity = 0;
                    if (!TextUtils.isEmpty(quantityStr))
                    {
                        try
                        {
                            quantity = (int) Double.parseDouble(quantityStr);
                        }
                        catch (NumberFormatException e)
                        {
                            Quantity.setError("Enter a valid number");
                            return;
                        }
                    }

                    Ingredient item = isEdit ? existing : new Ingredient();
                    item.setName(name);
                    item.setBrand(brand);
                    item.setQuantity(quantity);
                    item.setUnit(unit);
                    item.setExpiryDate(selectedExpiryDate);

                    if (isEdit)
                    {
                        int updatedRows = database.updateIngredient(item);

                        if (updatedRows == 0)
                        {
                            Toast.makeText(requireContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(requireContext(), name + " updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        item.setDate(new Date());

                        long id = database.insertIngredient(item);

                        if (id == -1)
                        {
                            Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(requireContext(), name + " added", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                    selectedIngredient = null;
                    refreshList();
                }));

        dialog.show();
    }

    private void showDatePicker(EditText target)
    {
        Calendar calendar = Calendar.getInstance();
        if (selectedExpiryDate != null)
        {
            calendar.setTime(selectedExpiryDate);
        }

        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) ->
                {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth, 0, 0, 0);
                    picked.set(Calendar.MILLISECOND, 0);

                    selectedExpiryDate = picked.getTime();
                    target.setText(displayFormat.format(selectedExpiryDate));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        picker.show();
    }

    private void refreshList()
    {
        selectedIngredient = null;
        adapter.setItems(database.getAllIngredients());
    }
}