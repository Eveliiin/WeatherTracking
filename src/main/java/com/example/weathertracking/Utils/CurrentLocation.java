package com.example.weathertracking.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.weathertracking.models.FavoriteLocationObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;

public class CurrentLocation {
    private static String LAST_CURRENT_LOCATIO_KEY = "LAST_CURRENT_LOCATIO_KEY";
    private static String sharedPrefFile =
            "current_location";
    private static SharedPreferences mPreferences;

    public static FavoriteLocationObject getCurrentLocationFromSharedPref(Context ctx) {

        mPreferences = ctx.getSharedPreferences( sharedPrefFile, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPreferences.getString(LAST_CURRENT_LOCATIO_KEY,null);
        Type type = new TypeToken<FavoriteLocationObject>() {
        }.getType();
        return gson.fromJson(json, type);

    }
    public static void setLastCurrentLocation(FavoriteLocationObject currentLocationObject, Context ctx) {

        //deleteAll(ctx);
        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        Gson gson = new Gson();
        Type type = new TypeToken<FavoriteLocationObject>() {}.getType();
        String json = gson.toJson(currentLocationObject, type);

        editor.putString(LAST_CURRENT_LOCATIO_KEY, json);
        editor.apply();
    }
}
