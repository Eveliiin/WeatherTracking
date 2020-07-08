package com.example.weathertracking.weatherApi.weather;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Weather implements Serializable {
    @SerializedName("id")
    public int id;
    @SerializedName("main")//kintre
    public String main;
    @SerializedName("description")//detailsnek
    public String description;
    @SerializedName("icon")
    public String icon;

}
