package com.example.pantry;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pantry.fragments.Pantry;
import com.example.pantry.fragments.Recipe;

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
                return new Pantry();
            case 1:
                return new Recipe();
            default:
                return new Pantry();
        }
    }

    @Override
    public int getItemCount()
    {
        return 2;
    }
}
