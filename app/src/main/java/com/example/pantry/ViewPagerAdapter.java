package com.example.pantry;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pantry.fragments.PantryList;
import com.example.pantry.fragments.RecipeList;

public class ViewPagerAdapter extends FragmentStateAdapter
{
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 0:
                return new PantryList();
            case 1:
                return new RecipeList();
            default:
                return new PantryList();
        }
    }

    @Override
    public int getItemCount()
    {
        return 2;
    }
}
