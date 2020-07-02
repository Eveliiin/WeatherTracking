package com.example.weathertracking.sevicesAndReceiver;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.weathertracking.weatherApi.CurrentWeather;
import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.models.Forecast;

import java.util.ArrayList;
import java.util.List;

import static com.example.weathertracking.network.ConnectionStateMonitor.isConnectedToInternet;
import static com.example.weathertracking.ui.main.MainActivity.isLocationGranted;
import static com.example.weathertracking.ui.main.details.LocationDetailFragment.CURRENT_LOCATION_TYPE;
import static com.example.weathertracking.ui.main.details.LocationDetailFragment.FAVORITE_LOCATION_OBJECT_TYPE;
import static com.example.weathertracking.sharedPrefAccess.CurrentLocation.getCurrentLocationFromSharedPref;
import static com.example.weathertracking.sharedPrefAccess.CurrentLocation.setLastCurrentLocationCurrentWeather;
import static com.example.weathertracking.sharedPrefAccess.CurrentLocation.setLastCurrentLocationForecast;
import static com.example.weathertracking.sharedPrefAccess.Favorites.getFavoriteLocationsFromSharedPref;
import static com.example.weathertracking.sharedPrefAccess.Favorites.updateFavoriteCurrentWeather;
import static com.example.weathertracking.sharedPrefAccess.Favorites.updateFavoriteForecast;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.CurrentWeatherCall.getCurrentWeatherByLatLng;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.WeatherForecastCalls.getForecastByLatlng;

public class AlarmReceiver extends BroadcastReceiver {


    private BroadcastReceiver currentWeatherReceiver;
    private BroadcastReceiver forecastReceiver;

    private IntentFilter filter;
    private IntentFilter weatherOkFilter;

    private ArrayList<FavoriteLocationObject> favorites;
    private int weatherSetNum;
    private boolean currentWeatherReceived;

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        if(isConnectedToInternet()) {
            Toast.makeText(context, "REFRESSH", Toast.LENGTH_SHORT).show();
            deliverNotification(context);
        }
    }

    private void deliverNotification(Context context) {

        try {//TODO favorites refresh!!
            if (isLocationGranted) {
                currentWeatherReceived = false;
                Intent refreshLocationIntent = new Intent(context, LocationService.class);
                refreshLocationIntent.putExtra("Command", "REFRESH_LOCATION");

                context.startService(refreshLocationIntent);
                Log.d("Location**", "refresh alarm");
            } else {
                currentWeatherReceived = true; //it means we dont need to receive current weather
            }
            weatherSetNum = 0;
            initReceivers();
            LocalBroadcastManager.getInstance(mContext).registerReceiver(currentWeatherReceiver, filter);
            LocalBroadcastManager.getInstance(mContext).registerReceiver(forecastReceiver, weatherOkFilter);

            favorites = getFavoriteLocationsFromSharedPref(context);
            for (int i = 0; i < favorites.size(); i++) {
                getCurrentWeatherByLatLng(context, favorites.get(i).getLatLng(), i);
                getForecastByLatlng(context, favorites.get(i).getLatLng(), i);
            }
            FavoriteLocationObject favoriteLocationObject = getCurrentLocationFromSharedPref(mContext);
            getCurrentWeatherByLatLng(context, favoriteLocationObject.getLatLng(), CURRENT_LOCATION_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initReceivers() {

        //bindban hasitotablaval
        filter = new IntentFilter("CURRENT_WEATHER_F");
        filter.addAction("CURRENT_WEATHER" + CURRENT_LOCATION_TYPE);

        currentWeatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if ("CURRENT_WEATHER_F".equals(action)) {
                    CurrentWeather currentWeather = (CurrentWeather) intent.getSerializableExtra("CURRENT_WEATHER_OBJECT");
                    int index;
                    if (currentWeather != null) {
                        index = currentWeather.getPosition();

                        updateFavoriteCurrentWeather(index, mContext, currentWeather);
                        weatherSetNum++;
                        if (weatherSetNum == favorites.size() && currentWeatherReceived) {
                            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(currentWeatherReceiver);
                        }
                    }
                } else {
                    Log.e("FAVORITELISTADAPTER", "null weather received");
                }
                if (("CURRENT_WEATHER" + CURRENT_LOCATION_TYPE).equals(action)) {

                    CurrentWeather currentWeather = (CurrentWeather) intent.getSerializableExtra("CURRENT_WEATHER_OBJECT");
                    if (currentWeather != null) {
                        setLastCurrentLocationCurrentWeather(currentWeather, mContext);

                    } else {
                        Log.e("location detail fragment", "null weather object");
                    }
                    LocalBroadcastManager.getInstance(mContext).unregisterReceiver(currentWeatherReceiver);

                }
            }
        };

        weatherOkFilter = new IntentFilter("WEATHER_OK" + FAVORITE_LOCATION_OBJECT_TYPE);

        forecastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (("WEATHER_OK" + CURRENT_LOCATION_TYPE).equals(action)) {

                    Forecast forecast = (Forecast) intent.getSerializableExtra("FORECAST_OBJECT");
                    if (forecast != null) {
                        setLastCurrentLocationForecast(forecast, mContext);
                        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(forecastReceiver);
                    }
                } else {
                    if ("WEATHER_OK_FAVORITE_LIST".equals(action)) {
                        Forecast forecast = (Forecast) intent.getSerializableExtra("FORECAST_OBJECT");
                        int index = intent.getIntExtra("INDEX", -1);
                        if (index > -1) {
                            updateFavoriteForecast(index, mContext, forecast);
                        }

                    }
                }
            }
        };
    }
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
