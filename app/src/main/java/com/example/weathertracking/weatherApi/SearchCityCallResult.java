package com.example.weathertracking.weatherApi;

import com.example.weathertracking.weatherApi.weather.City;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchCityCallResult implements Serializable {
    @SerializedName("cod")
    public String cod;
    @SerializedName("message")
    public String message;
    @SerializedName("cnt")
    public int cnt;
    @SerializedName("list")
    public List<SearchCityObject> list;
    @SerializedName("city")
    public City city;
}
