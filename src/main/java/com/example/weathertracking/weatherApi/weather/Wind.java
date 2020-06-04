package com.example.weathertracking.weatherApi.weather;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Wind implements Serializable {
    @SerializedName("speed")
    public float speed;  ///detail
    @SerializedName("deg")
    public float deg;
}
