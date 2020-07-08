package com.example.weathertracking.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weathertracking.models.HourlyWeather;
import com.example.weathertracking.R;

import java.util.List;

public class HourlyWeatherListFragment extends Fragment {

    List<HourlyWeather> weathers;

    public HourlyWeatherListFragment(List<HourlyWeather> weathers) {
        // Required empty public constructor
        this.weathers=weathers;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_hourly_weather_list, container, false);


        RecyclerView recyclerView= view.findViewById(R.id.recyclerView);


        /*final HourlyDataAdapter adapter = new HourlyDataAdapter(getContext(),weathers,11111111);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));*/
        recyclerView.setHasFixedSize(true);

        return view;
    }

}
