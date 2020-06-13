package com.example.weathertracking.weatherApi;

import com.example.weathertracking.weatherApi.CurrentWeatherCallResult;
import com.example.weathertracking.weatherApi.SearchCityCallResult;
import com.example.weathertracking.weatherApi.WeatherForecastCallResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather?")
    Call<CurrentWeatherCallResult>getCurrentWeatherDataLatLng(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String app_id);


    @GET("data/2.5/weather?")
    Call<CurrentWeatherCallResult>getCurrentWeatherDataByName(@Query("q") String city, @Query("APPID") String app_id);


    @GET("data/2.5/forecast")
    Call<WeatherForecastCallResult>getCurrentForecastData(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String app_id);

    @GET("data/2.5/forecast")
    Call<WeatherForecastCallResult>getCurrentForecastDataByName(@Query("q") String location, @Query("APPID") String app_id);


    @GET("data/2.5/find")
    Call<SearchCityCallResult>getCitiesBySearchTag(@Query("q") String searchTag, @Query("type") String type, @Query("cnt") String cnt, @Query("APPID") String app_id);

}
