package com.example.weathertracking.models;

public class HourlyWeather {
    private String data;
    private String temperature;
    private String feelsLike;
    private String icon;
    private String weatherData;
    private String wind;
    private String humidity;
    private String pressure;

    public HourlyWeather(String data, String temperature, String feelsLike, String icon, String weatherData, String wind, String humidity, String pressure) {
        this.data = data;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.icon = icon;
        this.weatherData = weatherData;
        this.wind = wind;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(String weatherData) {
        this.weatherData = weatherData;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}
