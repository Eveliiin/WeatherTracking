package com.example.weathertracking.Utils;

import com.example.weathertracking.weatherApi.WeatherForecastObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Forecast implements Serializable {


    @SerializedName("today")
    @Expose
    private ArrayList<WeatherForecastObject> today;
    @SerializedName("tomorrow")
    @Expose
    private ArrayList<WeatherForecastObject> tomorrow;
    @SerializedName("forecast")
    @Expose
    private ArrayList<WeatherForecastObject> forecast;

   public Forecast(ArrayList today,ArrayList tomorrow,ArrayList forecast){
        this.today=today;
        this.tomorrow=tomorrow;
        this.forecast=forecast;

    }

    public void setToday(ArrayList today) {
        this.today = new ArrayList(today);
    }

    public void setTomorrow(ArrayList tomorrow) {
        this.tomorrow = new ArrayList(tomorrow);
    }

    public void setForecast(ArrayList forecast) {
        this.forecast = new ArrayList(forecast);
    }

    public ArrayList getToday() {
        return today;
    }

    public ArrayList getTomorrow() {
        return tomorrow;
    }

    public ArrayList getForecast() {
        return forecast;
    }
}
