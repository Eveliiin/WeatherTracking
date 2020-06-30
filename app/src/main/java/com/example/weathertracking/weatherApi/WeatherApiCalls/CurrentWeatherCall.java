package com.example.weathertracking.weatherApi.WeatherApiCalls;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.weathertracking.models.CurrentWeather;
import com.example.weathertracking.weatherApi.WeatherService;
import com.example.weathertracking.weatherApi.CurrentWeatherCallResult;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.weathertracking.weatherApi.WeatherCallMembers.AppId;
import static com.example.weathertracking.weatherApi.WeatherCallMembers.BaseUrl;


public class CurrentWeatherCall {
    public static final int LOCATION_SEARCH_A=-1;
    public static final int LOCATION_DETAIL_F=-2;
    public static final int LOCATION_DETAIL_F_C=-3;


    public static void getCurrentWeatherByLatLng(Context context, LatLng latLng, int positionOrTypeID) {

        new SimpleAsyncTask(context, latLng, positionOrTypeID).execute();//doInBackGroundnak tovabbit a .execute
    }


    private static class SimpleAsyncTask extends AsyncTask<Void, Integer, Void> {//params(doinbckgrnd), progress(update), result(postexec)

        private LatLng latLng;
        private Context mContext;
        private Integer positionOrTypeID;

        SimpleAsyncTask(Context context, LatLng latLng, int positionOrTypeID) {
            this.latLng = latLng;
            mContext = context;
            this.positionOrTypeID = positionOrTypeID;
        }

        @Override
        protected Void doInBackground(Void... voids) {//nem lehet frissiteni a UI-t

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            WeatherService service = retrofit.create(WeatherService.class);
            Call<CurrentWeatherCallResult> call = service.getCurrentWeatherDataLatLng(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), AppId);
            call.enqueue(new Callback<CurrentWeatherCallResult>() {
                @Override
                public void onResponse(@NonNull Call<CurrentWeatherCallResult> call, @NonNull Response<CurrentWeatherCallResult> response) {
                    if (response.code() == 200 && response.body() != null) {//TODO DEFINE 200

                        CurrentWeatherCallResult currentWeatherCallResult = response.body();
                        CurrentWeather currentWeather =
                                new CurrentWeather(
                                        positionOrTypeID,
                                        currentWeatherCallResult.weather.get(0).description,
                                        currentWeatherCallResult.weather.get(0).icon,
                                        String.valueOf(Math.round((double) currentWeatherCallResult.main.temp_max - 273)),
                                        String.valueOf(Math.round((double) currentWeatherCallResult.main.temp_min - 273)),
                                        String.valueOf(Math.round((double) currentWeatherCallResult.main.temp - 273)),
                                        currentWeatherCallResult.name,
                                        currentWeatherCallResult.weather.get(0).id);
                        Intent i;
                        if(positionOrTypeID>-1){
                            i = new Intent("CURRENT_WEATHER_F");
                        }
                        else{
                            i = new Intent("CURRENT_WEATHER"+positionOrTypeID);
                        }

                        i.putExtra("CURRENT_WEATHER_OBJECT", currentWeather);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CurrentWeatherCallResult> call, @NonNull Throwable t) {
                    t.getMessage();
                }
            });
            return null;
        }

        /*
        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            //mTextView.get().setText(values[0].toString());

            //mProgressbar.get().setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            //mProgressbar.get().setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String s) {//lehet frissiteni a UI-t
            //mTextView.get().setText(s);
            //mProgressbar.get().setVisibility(View.INVISIBLE);
        }*/


    }

}