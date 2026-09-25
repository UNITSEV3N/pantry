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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        database = new Database(requireContext());
        database.resetDatabase();

        View view =  inflater.inflate(R.layout.fragment_pantry_list, container, false);
        view.findViewById(R.id.addFloatingButton).setOnClickListener(v -> showAddIngredientDialog());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new IngredientAdapter(database.getAllIngredients());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void showAddIngredientDialog()
    {
        selectedExpiryDate = null;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_ingredient_dialog, null);

        EditText Name = dialogView.findViewById(R.id.IngredientName);
        EditText Brand = dialogView.findViewById(R.id.IngredientBrand);
        EditText Quantity = dialogView.findViewById(R.id.IngredientQuantity);
        EditText Expiry = dialogView.findViewById(R.id.IngredientExpiry);

        Expiry.setOnClickListener(v -> showDatePicker(Expiry));

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
                    String quantityStr = Quantity.getText().toString().trim();

                    if (TextUtils.isEmpty(name))
                    {
                        Name.setError("Required");
                        return;
                    }

                    int quantity = 0;
                    if (!TextUtils.isEmpty(quantityStr))
                    {
                        quantity = Integer.parseInt(quantityStr);
                    }

                    Ingredient item = new Ingredient();
                    item.setName(name);
                    item.setBrand(brand);
                    item.setQuantity(quantity);
                    item.setDate(new Date());
                    item.setExpiryDate(selectedExpiryDate);

                    long id = database.insertIngredient(item);

                    if (id == -1)
                    {
                        Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(requireContext(), name + " added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    refreshList();
                }));

        dialog.show();
    }
    private void showDatePicker(EditText target)
    {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) ->
                {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth, 0, 0, 0);
                    picked.set(Calendar.MILLISECOND, 0);

                    selectedExpiryDate = picked.getTime();
                    target.setText(displayFormat.format(selectedExpiryDate));

                    Toast.makeText(requireContext(), "Picked: " + selectedExpiryDate, Toast.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        picker.show();
    }

    private void refreshList()
    {
        adapter.setItems(database.getAllIngredients());
    }
}