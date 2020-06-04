package com.example.weathertracking.weatherApi;

import com.example.weathertracking.weatherApi.weather.Clouds;
import com.example.weathertracking.weatherApi.weather.Coord;
import com.example.weathertracking.weatherApi.weather.Main;
import com.example.weathertracking.weatherApi.weather.Rain;
import com.example.weathertracking.weatherApi.weather.Snow;
import com.example.weathertracking.weatherApi.weather.Sys;
import com.example.weathertracking.weatherApi.weather.Weather;
import com.example.weathertracking.weatherApi.weather.Wind;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchCityObject implements Serializable {///TODO Model

    @SerializedName("dt")

    public int dt ;
    @SerializedName("main")

    public Main main ;
    @SerializedName("weather")
    public List<Weather> weather ;
    @SerializedName("clouds")
    public Clouds clouds;
    @SerializedName("wind")
    public Wind wind ;
    @SerializedName("rain")
    public Rain rain;

    @SerializedName("id")
    public  int id;
    @SerializedName("name")
    public String name;
    @SerializedName("coord")
    public Coord coord;

    @SerializedName("sys")
    public Sys sys ;

    @SerializedName("snow")
    public Snow snow;



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return coord;
    }

    public Main getMain() {
        return main;
    }

    public int getDt() {
        return dt;
    }

    public Wind getWind() {
        return wind;
    }

    public Sys getSys() {
        return sys;
    }

    public Rain getRain() {
        return rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public List<Weather> getWeather() {
        return weather;
    }
}

