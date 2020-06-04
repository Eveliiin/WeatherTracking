package com.example.weathertracking.weatherApi;

import com.example.weathertracking.weatherApi.weather.City;

import java.util.List;

public class WeatherForecastCallResult {
    public String cod;
    public double message;
    public int cnt;
    public List<WeatherForecastObject> list;
    public City city;
}

