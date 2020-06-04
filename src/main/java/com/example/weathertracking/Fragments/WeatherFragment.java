package com.example.weathertracking.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weathertracking.Adapters.HourlyDataAdapter;
import com.example.weathertracking.R;
import com.example.weathertracking.weatherApi.WeatherForecastObject;


import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    TextView locationTextView;
    Handler handler;
    List<WeatherForecastObject> weather;


    public WeatherFragment() {
        handler= new Handler();

    }
    public WeatherFragment(List<WeatherForecastObject> weather) {
        handler= new Handler();
        this.weather=weather;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_weather, container, false);

        RecyclerView recyclerView= view.findViewById(R.id.recyclerView);


        final HourlyDataAdapter adapter = new HourlyDataAdapter(getContext(),weather);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setHasFixedSize(true);


        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        //find_weather();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    private void renderWeather(JSONObject json){
        try {
            String text= json.getString("name").toUpperCase(Locale.US)+","+json.getJSONObject("sys").getString("country");
            locationTextView.setText(text);
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);

            setWeatherIcon(details.getInt("id"),json.getJSONObject("sys").getLong("sunrise")*1000,
                    json.getJSONObject("sys").getLong("sunset")*1000);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id= actualId/100;
        String icon="";
        if(actualId==800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise&& currentTime<sunset){
                icon =getActivity().getString(R.string.weather_sunny);

            }else {
                icon= getActivity().getString(R.string.weather_clear_night);
            }
        }
        else {
            switch (id){
                case 2: icon=getActivity().getString(R.string.weather_thunder);
                    break;
                case 3: icon=getActivity().getString(R.string.weather_drizzle);
                    break;
                case 5: icon=getActivity().getString(R.string.weather_rainy);
                    break;
                case 6: icon=getActivity().getString(R.string.weather_snowy);
                    break;
                case 7: icon=getActivity().getString(R.string.weather_foggy);
                    break;
                case 8: icon=getActivity().getString(R.string.weather_cloudy);
                    break;
            }
        }
        Log.d("ICON",icon);
        //weatherIcon.setText(icon);
    }
}
