package com.example.weathertracking.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.weathertracking.weatherApi.WeatherForecastObject;
import com.example.weathertracking.Fragments.WeatherFragment;

import java.util.List;

public class WeatherDaysPagerAdapter extends FragmentStatePagerAdapter {

    List<WeatherForecastObject>today;
    List<WeatherForecastObject>tomorrow;
    List<WeatherForecastObject>forecast;

    private int numOfTabs;
    public WeatherDaysPagerAdapter(@NonNull FragmentManager fm, int numOfTabs, List<WeatherForecastObject>today, List<WeatherForecastObject>tomorrow, List<WeatherForecastObject>forecast) {
        super(fm);
        this.numOfTabs=numOfTabs;
        this.today=today;
        this.tomorrow=tomorrow;
        this.forecast=forecast;


    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new WeatherFragment(today);
            case 1: return new WeatherFragment(tomorrow);
            case 2: return new WeatherFragment(forecast);
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
