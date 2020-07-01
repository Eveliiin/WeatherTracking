package com.example.weathertracking.ui.main.details;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weathertracking.adapters.HourlyDataAdapter;
import com.example.weathertracking.R;
import com.example.weathertracking.weatherApi.WeatherForecastObject;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherDetailsListFragment extends Fragment {

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;

    private Handler handler;
    private List<WeatherForecastObject> weather;

    public WeatherDetailsListFragment() {
        handler = new Handler();

    }

    public WeatherDetailsListFragment(List<WeatherForecastObject> weather) {
        handler = new Handler();
        this.weather = weather;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this, view);
        final HourlyDataAdapter adapter = new HourlyDataAdapter(getContext(), weather);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
