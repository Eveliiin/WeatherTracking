package com.example.weathertracking.weatherApi;

import com.example.weathertracking.weatherApi.weather.Clouds;
import com.example.weathertracking.weatherApi.weather.Coord;
import com.example.weathertracking.weatherApi.weather.Main;
import com.example.weathertracking.weatherApi.weather.Rain;
import com.example.weathertracking.weatherApi.weather.Sys;
import com.example.weathertracking.weatherApi.weather.Weather;
import com.example.weathertracking.weatherApi.weather.Wind;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CurrentWeatherCallResult {

    @SerializedName("coord")
    public Coord coord;
    @SerializedName("sys")
    public Sys sys;
    @SerializedName("weather")
    public ArrayList<Weather> weather = new ArrayList<Weather>();
    @SerializedName("main")
    public Main main;
    @SerializedName("wind")
    public Wind wind;
    @SerializedName("rain")
    public Rain rain;
    @SerializedName("clouds")
    public Clouds clouds;
    @SerializedName("dt")
    public float dt;
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("cod")
    public float cod;
}

