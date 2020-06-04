package com.example.weathertracking.weatherApi;

import com.example.weathertracking.weatherApi.weather.Main;
import com.example.weathertracking.weatherApi.weather.Weather;
import com.example.weathertracking.weatherApi.weather.Wind;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WeatherForecastObject implements Serializable {

    //@SerializedName("dt")
    //public int dt ;
    @SerializedName("main")
    public Main main ;
    @SerializedName("weather")
    public List<Weather> weather ;
    //@SerializedName("clouds")
    //public Clouds clouds;
    @SerializedName("wind")
    public Wind wind ;
    //@SerializedName("rain")
    //public Rain rain;
    //@SerializedName("sys")
    //public Sys sys ;
    @SerializedName("dt_txt")
    public String dt_txt;


}
