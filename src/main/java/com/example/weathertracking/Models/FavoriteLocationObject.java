package com.example.weathertracking.models;

import androidx.annotation.Nullable;

import com.example.weathertracking.Utils.Forecast;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.example.weathertracking.Utils.Favorites.df;

public class FavoriteLocationObject implements Serializable {

    @SerializedName("locationName")
    @Expose
    private String  locationName;

    //LatLng isn't serializable
    @SerializedName("latitude")
    @Expose
    private double  latitude;

    @SerializedName("longitude")
    @Expose
    private double  longitude;

    @SerializedName("currentWeather")
    @Expose
    private CurrentWeather currentWeather;

    @SerializedName("forecast")
    @Expose
    private Forecast forecast;


    public FavoriteLocationObject(String location, LatLng coord){
        this.locationName=location;
        this.latitude=coord.latitude;
        this.longitude=coord.longitude;
    }
    public FavoriteLocationObject( LatLng coord){
        this.latitude=coord.latitude;
        this.longitude=coord.longitude;
    }

    public FavoriteLocationObject(FavoriteLocationObject f) {
        this.locationName = f.getLocationName();
        this.latitude = f.getLatitude();
        this.longitude = f.getLongitude();
        this.currentWeather = f.getCurrentWeatherObject();
        this.forecast = f.getForecastObject();
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }
    public void setForecast(Forecast forecast){
        this.forecast=forecast;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FavoriteLocationObject simpson = (FavoriteLocationObject) o;
        latitude=Double.parseDouble(df.format(latitude));
        longitude=Double.parseDouble(df.format(longitude));
        return longitude == simpson.longitude &&
                latitude == latitude &&
                locationName.equals(simpson.locationName);
    }

    public LatLng getLatLng() {
        return new LatLng(latitude,longitude);
    }



    public void setLatLng(double latitude,double longitude) {
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public CurrentWeather getCurrentWeatherObject() {
        return currentWeather;
    }

    public Forecast getForecastObject() {
        return forecast;
    }

    public String  getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setIcon(String icon) {
        this.currentWeather.setIcon(icon);
    }
    public String getIcon() {
        return this.currentWeather.getIcon();
    }

    public String getWeather() {
        return this.currentWeather.getWeather();
    }

    public String getMaxTemp() {
        return this.currentWeather.getMaxTemp();
    }

    public String getMinTemp() {
        return  this.currentWeather.getMinTemp();
    }

    public String getCurrentTemp() {
        return  this.currentWeather.getCurrentTemp();
    }
}