package com.example.pantry.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pantry.R;
import com.example.pantry.database.Database;
import com.example.pantry.model.Ingredient;

import java.util.Date;

public class PantryList extends Fragment
{
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        database = new Database(requireContext());

        View view =  inflater.inflate(R.layout.fragment_pantry_list, container, false);
        view.findViewById(R.id.addFloatingButton).setOnClickListener(v -> showAddIngredientDialog());

        return view;
    }

    private void showAddIngredientDialog()
    {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_ingredient_dialog, null);

        EditText Name = dialogView.findViewById(R.id.IngredientName);
        EditText Brand = dialogView.findViewById(R.id.IngredientBrand);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add ingredient")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                {
                    String name = Name.getText().toString().trim();
                    String brand = Brand.getText().toString().trim();

                    if (TextUtils.isEmpty(name))
                    {
                        Name.setError("Required");
                        return;
                    }

                    Ingredient item = new Ingredient();
                    item.setName(name);
                    item.setBrand(brand);
                    item.setDate(new Date());

                    long id = database.insertIngredient(item);

                    if (id == -1)
                    {
                        Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(requireContext(), name + " added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }));

        dialog.show();
    }
}