package com.example.weathertracking.weatherApi.WeatherApiCalls;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.weathertracking.models.Forecast;
import com.example.weathertracking.weatherApi.WeatherService;
import com.example.weathertracking.weatherApi.WeatherForecastObject;
import com.example.weathertracking.weatherApi.WeatherForecastCallResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.weathertracking.weatherApi.WeatherCallMembers.AppId;
import static com.example.weathertracking.weatherApi.WeatherCallMembers.BaseUrl;

public class WeatherForecastCalls  implements Serializable {

    @SerializedName("GPS")
    @Expose
    private  static final String GPS_TYPE ="GPS";
    @SerializedName("NAME")
    @Expose
    private static final String NAME_TYPE ="NAME";



    public static void getForecastByLatlng(final Context context, LatLng latLng) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherForecastCallResult> call;
        call = service.getCurrentForecastData(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude), AppId);
        //call = service.getCurrentForecastDataByName(typeOrLocation, AppId);

        call.enqueue(new Callback<WeatherForecastCallResult>() {
            @Override
            public void onResponse(@NonNull Call<WeatherForecastCallResult> call, @NonNull Response<WeatherForecastCallResult> response) {
                if (response.code() == 200 && response.body()!=null) {
                    WeatherForecastCallResult weatherForecastCallResult = response.body();
                    List<WeatherForecastObject> hours = new ArrayList<>(weatherForecastCallResult.list);

                    String cityName= weatherForecastCallResult.city.name;
                    Calendar date = Calendar.getInstance();
                    int currentDay = date.get(Calendar.DAY_OF_MONTH);
                    date.add(Calendar.DATE, 1);
                    int nextDay =date.get(Calendar.DAY_OF_MONTH);

                    ArrayList<WeatherForecastObject> today = new ArrayList<>();
                    ArrayList<WeatherForecastObject> tomorrow= new ArrayList<>();
                    ArrayList<WeatherForecastObject> forecast = new ArrayList<>();
                    for(WeatherForecastObject i : hours){

                        int day = Integer.parseInt(i.dt_txt.substring(8,10));
                        if(currentDay == day )
                        {
                            i.dt_txt=i.dt_txt.substring(11,16);
                            today.add(i);
                            Log.d("weatherdata","today- "+i.toString());
                        }
                        else {
                            if(nextDay == day) {
                                i.dt_txt=i.dt_txt.substring(11,16);

                                tomorrow.add(i);
                                Log.d("weatherdata","tomorrow- " +i.toString());
                            }
                            else {

                                i.dt_txt=new DateFormatSymbols().getMonths()[Integer.valueOf(i.dt_txt.substring(5,7))-1]+" "+Integer.valueOf(i.dt_txt.substring(8,10))+" "+i.dt_txt.substring(11,16);

                                forecast.add(i);
                                Log.d("weatherdata","forecast- " + i.toString());
                            }
                        }
                        // TODO Toast.makeText((),"Forecast weather refreshed!",Toast.LENGTH_SHORT).show();
                    }
                    Forecast forecastObject= new Forecast(today,tomorrow,forecast);

                        Intent i = new Intent("WEATHER_OK");
                        i.putExtra("LOCATION_NAME",cityName);
                        i.putExtra("FORECAST_OBJECT",forecastObject);
                        context.sendBroadcast(i);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherForecastCallResult> call, @NonNull Throwable t) {
                //weatherData.setText(t.getMessage());
            }
        });


    }
    private void startTask(String location) {
        //new SimpleAsyncTask(location).execute();//doInBackGroundnak tovabbit a .execute
    }


    /*private class SimpleAsyncTask extends AsyncTask<Void, Integer, Void> {//params(doinbckgrnd), progress(update), result(postexec)

        private String cityName;
        SimpleAsyncTask(String cityName){
            this.cityName=cityName;

        }


        @Override
        protected Void doInBackground(Void... voids) {//nem lehet frissiteni a UI-t

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            WeatherService service = retrofit.create(WeatherService.class);
            Call<CurrentWeatherCallResult> call = service.getCurrentWeatherDataByName(cityName, AppId);
            call.enqueue(new Callback<CurrentWeatherCallResult>() {
                @Override
                public void onResponse(@NonNull Call<CurrentWeatherCallResult> call, @NonNull Response<CurrentWeatherCallResult> response) {
                    if (response.code() == 200 && response.body() != null) {//TODO DEFINE 200

                        CurrentWeatherCallResult weatherResponse = response.body();
                        mCurrentWeather = weatherResponse.weather.get(0).description; //.main
                        mIconID = weatherResponse.weather.get(0).icon;

                        Intent i = new Intent("CURRENT_WEATHER");
                        i.putExtra("INDEX",mCurrentIndex);
                        mContext.sendBroadcast(i);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CurrentWeatherCallResult> call, @NonNull Throwable t) {
                    t.getMessage();
                }
            });

            //return "Awake at last after sleeping for " + s + " milliseconds!";
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
        }


    }*/
}
