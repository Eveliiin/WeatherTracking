package com.example.weathertracking.sharedPrefAccess;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.weathertracking.models.CurrentWeather;
import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.models.Forecast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Favorites  {

    public static DecimalFormat df=initDf();
    private static String FAVORITE_LIST_KEY = "FAVORITE_OBJECTS_LIST_KEY";
    private static SharedPreferences mPreferences;
    private static String sharedPrefFile =
            "favorite_locations";

    public Favorites(@NonNull Application application) {
        mPreferences = application.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

    }

    public static ArrayList<FavoriteLocationObject> getFavoriteLocationsFromSharedPref(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences( sharedPrefFile, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(FAVORITE_LIST_KEY,null);
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {
        }.getType();
        return gson.fromJson(json, type);

    }
    public static void  deleteAll(Context ctx){

        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {}.getType();
        String json = gson.toJson(new ArrayList<FavoriteLocationObject>(), type);

        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();


    }
    private static DecimalFormat initDf(){
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator('.');
        df = new DecimalFormat("#.##",formatSymbols);
        return df;
    }


    public static void updateFavorite(FavoriteLocationObject favorite,Context ctx){

        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        ArrayList<FavoriteLocationObject> favorites;
        if(getFavoriteLocationsFromSharedPref(ctx) == null){
            Log.d("favorites", "Empty favorites array");
            return;
        }
        else {
            favorites= getFavoriteLocationsFromSharedPref(ctx);
            if(favorites.contains(favorite)){
                favorites.set(favorites.indexOf(favorite),favorite);
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {}.getType();
        String json = gson.toJson(favorites, type);

        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();
    }

    public static void updateFavoriteCurrentWeather(int index, Context ctx, CurrentWeather currentWeather){

        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        ArrayList<FavoriteLocationObject> favorites;
        if(getFavoriteLocationsFromSharedPref(ctx) == null){
            Log.d("favorites", "Empty favorites array");
            return;
        }
        else {
            favorites= getFavoriteLocationsFromSharedPref(ctx);
            if(favorites.get(index)!=null){
                favorites.get(index).setCurrentWeather(currentWeather);
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {}.getType();
        String json = gson.toJson(favorites, type);

        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();
    }

    public static void updateFavoriteForecast(int index, Context ctx, Forecast forecast){

        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        ArrayList<FavoriteLocationObject> favorites;
        if(getFavoriteLocationsFromSharedPref(ctx) == null){
            Log.d("favorites", "Empty favorites array");
            return;
        }
        else {
            favorites= getFavoriteLocationsFromSharedPref(ctx);
            if(favorites.get(index)!=null){
                favorites.get(index).setForecast(forecast);
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {}.getType();
        String json = gson.toJson(favorites, type);

        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();
    }

    public static void modifyFavorite(FavoriteLocationObject newFavorite, Context ctx) {

        //deleteAll(ctx);

        newFavorite.setLatLng(Double.parseDouble(df.format(newFavorite.getLatitude())),Double.parseDouble(df.format(newFavorite.getLongitude())));

        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        ArrayList<FavoriteLocationObject> favorites= new ArrayList<FavoriteLocationObject>() {};
        if(getFavoriteLocationsFromSharedPref(ctx) == null){
            Log.d("favorites", "Empty favorites array");
            favorites.add(newFavorite);
            Toast.makeText(ctx,newFavorite.getLocationName()+ " added to favorites",Toast.LENGTH_SHORT).show();
        }
        else {
            favorites= getFavoriteLocationsFromSharedPref(ctx);
            if(favorites.contains(newFavorite)){
                Toast.makeText(ctx,newFavorite.getLocationName()+ " removed from favorites",Toast.LENGTH_SHORT).show();
                favorites.remove(newFavorite);
            }
            else {
                favorites.add(newFavorite);
                Toast.makeText(ctx,newFavorite.getLocationName()+ " added to favorites",Toast.LENGTH_SHORT).show();
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {}.getType();
        String json = gson.toJson(favorites, type);

        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();

        Intent intent = new Intent();
        intent.setAction("REFRESH_FAVORITES");
        ctx.sendBroadcast(intent);
    }

    public static void deleteFavorite(FavoriteLocationObject newFavorite, Context ctx) {

        //deleteAll(ctx);

        newFavorite.setLatLng(Double.parseDouble(df.format(newFavorite.getLatitude())),Double.parseDouble(df.format(newFavorite.getLongitude())));

        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        ArrayList<FavoriteLocationObject> favorites= new ArrayList<FavoriteLocationObject>() {};
        if(getFavoriteLocationsFromSharedPref(ctx) != null) {

                favorites = getFavoriteLocationsFromSharedPref(ctx);
                if (favorites.contains(newFavorite)) {
                    Toast.makeText(ctx, newFavorite.getLocationName() + " removed from favorites", Toast.LENGTH_SHORT).show();
                    favorites.remove(newFavorite);
                } else {
                    Toast.makeText(ctx,newFavorite.getLocationName()+" is not favorite \n unable to delete",Toast.LENGTH_SHORT);
                }

        }
        else {
            Log.e("FAVORITES","empty favorites array - can't delete"+newFavorite.getLocationName());
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {}.getType();
        String json = gson.toJson(favorites, type);

        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();

        Intent intent = new Intent();
        intent.setAction("REFRESH_FAVORITES");
        ctx.sendBroadcast(intent);
    }

    public static void addFavorite(FavoriteLocationObject newFavorite, Context ctx) {

        //deleteAll(ctx);

        newFavorite.setLatLng(Double.parseDouble(df.format(newFavorite.getLatitude())),Double.parseDouble(df.format(newFavorite.getLongitude())));

        mPreferences = ctx.getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        ArrayList<FavoriteLocationObject> favorites= new ArrayList<FavoriteLocationObject>() {};
        if(getFavoriteLocationsFromSharedPref(ctx) == null){
            Log.d("favorites", "Empty favorites array");
            favorites.add(newFavorite);
            Toast.makeText(ctx,newFavorite.getLocationName()+ " added to favorites",Toast.LENGTH_SHORT).show();
        }
        else {
            favorites= getFavoriteLocationsFromSharedPref(ctx);
            if(favorites.contains(newFavorite)){
                Toast.makeText(ctx,newFavorite.getLocationName()+"is already added to favorites",Toast.LENGTH_SHORT);
            }
            else {
                favorites.add(newFavorite);
                Toast.makeText(ctx,newFavorite.getLocationName()+ " added to favorites",Toast.LENGTH_SHORT).show();
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FavoriteLocationObject>>() {}.getType();
        String json = gson.toJson(favorites, type);

        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();

        Intent intent = new Intent();
        intent.setAction("REFRESH_FAVORITES");
        ctx.sendBroadcast(intent);
    }



    public static boolean checkIfIsFavorite(FavoriteLocationObject locationToCheck, Context ctx) {


        ArrayList<FavoriteLocationObject> favorites;
        try {
            favorites = getFavoriteLocationsFromSharedPref(ctx);
        } catch (Exception e) {
            Log.e("favorites", "Empty favorites array");
            return false;
        }
        if(locationToCheck==null ||favorites==null){//TODO bibi
            return false;
        }
        return favorites.contains(locationToCheck);
    }

    public void deleteOneFavorite(String favoriteToDelete,Context ctx) {

        SharedPreferences.Editor editor = ctx.getSharedPreferences(FAVORITE_LIST_KEY, MODE_PRIVATE).edit();
        ArrayList<FavoriteLocationObject> favorites= new ArrayList<FavoriteLocationObject>() {
        };
        try {
            favorites = getFavoriteLocationsFromSharedPref(ctx);
        } catch (Exception e) {
            Log.e("favorites", "Empty favorites array");
        }

        try{
            favorites.remove(favoriteToDelete);

        }
        catch ( Exception e)
        {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        editor.putString(FAVORITE_LIST_KEY, json);
        editor.apply();
    }


}
