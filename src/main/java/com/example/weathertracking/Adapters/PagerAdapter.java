package com.example.weathertracking.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.weathertracking.Fragments.FavoriteLocationsFragment;
import com.example.weathertracking.Fragments.LocationDetailFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: return new FavoriteLocationsFragment();
            case 1: return new LocationDetailFragment();
            default: return null;
        }
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
