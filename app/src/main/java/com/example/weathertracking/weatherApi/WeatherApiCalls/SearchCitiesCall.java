package com.example.weathertracking.weatherApi.WeatherApiCalls;

import android.content.Context;
import android.content.Intent;


import androidx.annotation.NonNull;

import com.example.weathertracking.weatherApi.WeatherService;
import com.example.weathertracking.weatherApi.SearchCityCallResult;
import com.example.weathertracking.weatherApi.SearchCityObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.weathertracking.weatherApi.WeatherCallMembers.AppId;
import static com.example.weathertracking.weatherApi.WeatherCallMembers.BaseUrl;


public class SearchCitiesCall {
    private static Context mContext;
    public static void searchLocation(String searchString, Context context) {//TODO rendes nevet a valtozonak
        mContext=context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<SearchCityCallResult> call;
        call = service.getCitiesBySearchTag(searchString, "like", "30", AppId);


        call.enqueue(new Callback<SearchCityCallResult>() {


            @Override
            public void onResponse(Call<SearchCityCallResult> call, Response<SearchCityCallResult> response) {
                if (response.code() == 200 && response.body() != null) {

                    final SearchCityCallResult weatherForecastResult = response.body();
                    ArrayList<SearchCityObject> results = new ArrayList<>(weatherForecastResult.list);

                    Intent i =  new Intent("SEARCH_RESULTS");
                    i.putExtra("SEARCH_RESULTS",results);
                    mContext.sendBroadcast(i);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchCityCallResult> call, @NonNull Throwable t) {
            }
        });
    }
}
