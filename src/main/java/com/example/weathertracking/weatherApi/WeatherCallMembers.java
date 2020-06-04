package com.example.weathertracking.weatherApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherCallMembers {
    @SerializedName("BaseUrl")
    @Expose
    public static String BaseUrl = "https://api.openweathermap.org/";
    @SerializedName("AppId")
    @Expose
    public static String AppId = "bafa394680e7655d011e7f0994898f01";

    }
