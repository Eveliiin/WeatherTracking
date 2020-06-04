package com.example.weathertracking.weatherApi.weather;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Main implements Serializable {
    @SerializedName("temp")
    public float temp;
     @SerializedName("feels_like")
     public float feels_like;
    @SerializedName("humidity")
    public float humidity;
    @SerializedName("pressure")
    public float pressure;
    @SerializedName("temp_min")
    public float temp_min;
    @SerializedName("temp_max")
    public float temp_max;
}
