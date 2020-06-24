package com.example.weathertracking.weatherApi.weatherEffects;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;


public class WeatherColours {
    @JsonProperty
    @SerializedName("code")
    public String code;

    @JsonProperty
    @SerializedName("description")
    public String description;

    @JsonProperty
    @SerializedName("icon")
    public String icon;

    @JsonProperty
    @SerializedName("startHex")
    public String startHex;

    @JsonProperty
    @SerializedName("endHex")
    public String endHex;

    @JsonProperty
    @SerializedName("rain")
    public int rain;

    @JsonProperty
    @SerializedName("snow")
    public int snow;


    WeatherColours(){

    }
    WeatherColours(String code, String description, String icon, String startHex, String endHex, int rain, int snow) {
        this.code = code;
        this.description = description;
        this.icon = icon;
        this.startHex = startHex;
        this.endHex = endHex;
        this.rain = rain;
        this.snow = snow;
    }
}
