package com.example.pantry;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.pantry.adapter.ViewPagerAdapter;
import com.example.pantry.database.Database;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity
{
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = new Database(this);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        findViewById(R.id.btn_settings).setOnClickListener(v -> showSettingsDialog());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);

                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void showSettingsDialog()
    {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_settings_menu, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Settings")
                .setView(dialogView)
                .setNegativeButton("Close", null)
                .create();

        dialogView.findViewById(R.id.resetDatabaseRow).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear Pantry?")
                        .setMessage("This will permanently clear pantry.")
                        .setPositiveButton("Reset", (d, which) ->
                        {
                            // Clears only the ingredients
                            database.clearPantry();

                            Toast.makeText(this, "Database reset", Toast.LENGTH_SHORT).show();
                            getSupportFragmentManager().setFragmentResult("database_reset", Bundle.EMPTY);
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        dialog.show();
    }
}