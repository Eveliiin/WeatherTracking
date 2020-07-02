package com.example.weathertracking.weatherApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;

public class CurrentWeather implements Serializable {

    public void setPosition(Integer position) {
        this.position = position;
    }

    @SerializedName("position")
    @Expose
    private Integer position;

    @SerializedName("weather")
    @Expose
    private String weather = "no data";

    @SerializedName("weatherId")
    @Expose
    private int weatherId;


    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("minTemp")
    @Expose
    private String minTemp;

    @SerializedName("maxTemp")
    @Expose
    private String maxTemp;

    @SerializedName("currentTemp")
    @Expose
    private String currentTemp;

    @SerializedName("locationName")
    @Expose
    private String locationName;

    @SerializedName("lastrefreshDate")
    @Expose
    private String lastrefreshDate;


    public CurrentWeather(Integer position, String weather, String icon, String minTemp, String maxTemp, String currentTemp, String locationName, int weatherId) {
        this.position = position;
        this.weather = StringUtils.capitalize(weather);
        this.weatherId = weatherId;
        this.icon = icon;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.currentTemp = currentTemp;
        this.locationName = locationName;

        LocalDateTime now = LocalDateTime.now();
        String month = new DateFormatSymbols().getMonths()[now.getMonthValue() - 1];
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        this.lastrefreshDate = "Refreshed on " + day + ". " + month + " " + hour + ":" + minute;
    }

    public Integer getPosition() {
        return position;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLastrefreshDate() {
        return lastrefreshDate;
    }

}
