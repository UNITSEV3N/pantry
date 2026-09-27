package com.example.pantry.adapter;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pantry.R;
import com.example.pantry.database.Database;

public class SettingMenuAdapter extends Fragment
{
    private Database database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        database = new Database(requireContext());

        View view = inflater.inflate(R.layout.fragment_settings_menu, container, false);

        view.findViewById(R.id.resetDatabaseRow).setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Reset database?")
                        .setMessage("This will permanently delete all ingredients and recipes. This can't be undone.")
                        .setPositiveButton("Reset", (dialog, which) ->
                        {
                            database.resetDatabase();
                            Toast.makeText(requireContext(), "Database reset", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        return view;
    }
}