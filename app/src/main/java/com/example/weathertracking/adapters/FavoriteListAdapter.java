package com.example.weathertracking.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weathertracking.models.Forecast;
import com.example.weathertracking.ui.main.LocationDetailActivity;
import com.example.weathertracking.weatherApi.CurrentWeather;
import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.R;
import com.example.weathertracking.weatherApi.weatherEffects.Icons;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.example.weathertracking.ui.main.details.LocationDetailFragment.FAVORITE_LOCATION_OBJECT;
import static com.example.weathertracking.sharedPrefAccess.Favorites.updateFavoriteCurrentWeather;
import static com.example.weathertracking.sharedPrefAccess.Favorites.updateFavoriteForecast;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.CurrentWeatherCall.getCurrentWeatherByLatLng;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.WeatherForecastCalls.getForecastByLatlng;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.WeatherViewHolder> implements Filterable {

    private static ArrayList<FavoriteLocationObject> mFavorites = new ArrayList<>();
    private static ArrayList<FavoriteLocationObject> mFavoritesFull = new ArrayList<>();

    private final LayoutInflater mInflater;
    private Context mContext;
    private int weatherSetNum;
    private int forecastSetNum;
    private IntentFilter filter;
    private BroadcastReceiver currentWeatherReceiver;
    private boolean currentWeatherReceiverIsRegistered=false;
    private boolean forecastReceiverIsRegistered=false;
    private IntentFilter weatherOkFilter;
    private BroadcastReceiver forecastReceiver;
    private Hashtable<String, WeatherViewHolder> holderHashtable = new Hashtable<>();
    private static Hashtable<FavoriteLocationObject, ImageView> locationWiconsHashTable = new Hashtable<>();
    private static ArrayList<Boolean> isBinded;

    public FavoriteListAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        weatherSetNum = 0;
        forecastSetNum=0;
        initReceiver();
    }

    //Todo refresh everything
    public void refreshCurrentWeathersInFavorites(ArrayList<FavoriteLocationObject> favorites) {
        mFavorites = new ArrayList<>(favorites);
        mFavoritesFull = new ArrayList<>(mFavorites);
        if (holderHashtable.size() == 0) {

            if(!currentWeatherReceiverIsRegistered) {
                LocalBroadcastManager.getInstance(mContext).registerReceiver(currentWeatherReceiver, filter);
                currentWeatherReceiverIsRegistered=true;
            }

            weatherSetNum = 0;
            forecastSetNum= 0;
            mFavorites = new ArrayList<>(favorites);
        }
        isBinded = new ArrayList<>();
        for (int i = 0; i <= favorites.size(); i++) {
            isBinded.add(i, false);
        }
        weatherSetNum = 0;
        forecastSetNum=0;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @NonNull
    @Override
    // inflates the row layout from xml when needed
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.favorite_item, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, final int position) {

        FavoriteLocationObject currentFavorite = mFavorites.get(position);
        String location = currentFavorite.getLocationName();

        if (isBinded.size() < position) {

            registerCurrentWeatherReceiver();
            registerForecastReceiver();
            getCurrentWeatherByLatLng(mContext, currentFavorite.getLatLng(), position);
            getForecastByLatlng(mContext, currentFavorite.getLatLng(), position);
            isBinded.add(position, true);
        }
        if (!isBinded.get(position)) {

            registerCurrentWeatherReceiver();
            registerForecastReceiver();

            getCurrentWeatherByLatLng(mContext, currentFavorite.getLatLng(), position);
            getForecastByLatlng(mContext, currentFavorite.getLatLng(), position);
            isBinded.set(position, true);
        }
        String weather = "no data";
        holderHashtable.put(location, holder);
        if (currentFavorite.getCurrentWeatherObject() != null) {

            weather = currentFavorite.getWeather();
            changeIcon(mContext, holder.iconImageView, currentFavorite.getIcon());
            holder.tempMinTV.setText(currentFavorite.getMinTemp());
            holder.tempMaxTV.setText(currentFavorite.getMaxTemp());
        }
        //
        else {
            registerCurrentWeatherReceiver();
            getCurrentWeatherByLatLng(mContext, currentFavorite.getLatLng(), position);
        }

        holder.weatherLocationItemView.setText(location);
        holder.weatherDataItemView.setText(weather);
        locationWiconsHashTable.put(mFavorites.get(position), holder.iconImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLocationDetailsActivity(mFavorites.get(position), mContext);
            }
        });
        //holder.iconImageView.setImageDrawable();
    }

    private void registerCurrentWeatherReceiver(){
        if(!currentWeatherReceiverIsRegistered) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(currentWeatherReceiver, filter);
            currentWeatherReceiverIsRegistered=true;
        }
    }
    private void registerForecastReceiver(){
        if(!forecastReceiverIsRegistered) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(forecastReceiver, weatherOkFilter);
            forecastReceiverIsRegistered=true;
        }
    }

    private void initReceiver() {

        //bindban hasitotablaval

        filter = new IntentFilter("CURRENT_WEATHER_F");
        currentWeatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if ("CURRENT_WEATHER_F".equals(action)) {
                    CurrentWeather currentWeather = (CurrentWeather) intent.getSerializableExtra("CURRENT_WEATHER_OBJECT");
                    int index;
                    if (currentWeather != null) {
                        index = currentWeather.getPosition();

                        mFavorites.get(index).setCurrentWeather(currentWeather);
                        updateFavoriteCurrentWeather(index,context,currentWeather);
                        changeIcon(mContext, locationWiconsHashTable.get(mFavorites.get(index)), mFavorites.get(index).getIcon());
                        weatherSetNum++;
                        if (weatherSetNum == mFavoritesFull.size()) {
                            currentWeatherReceiverIsRegistered=false;
                            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(currentWeatherReceiver);
                            Intent intenty = new Intent();
                            intent.setAction("REFRESH_FAVORITES");
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intenty);
                            //todo i$$$$$$$$$$ide az updatet
                        }
                        notifyDataSetChanged();
                    }
                } else {
                    Log.e("FAVORITELISTADAPTER", "null weather received");
                }
            }
        };
        weatherOkFilter = new IntentFilter("WEATHER_OK_FAVORITE_LIST");

        forecastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if ("WEATHER_OK_FAVORITE_LIST".equals(action)) {
                    forecastSetNum++;
                    Forecast forecast = (Forecast) intent.getSerializableExtra("FORECAST_OBJECT");
                    int index = intent.getIntExtra("INDEX", -1);
                    if (index > -1) {
                        updateFavoriteForecast(index, mContext, forecast);
                        if(forecastSetNum==mFavoritesFull.size()){
                            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(forecastReceiver);
                        }
                    }

                }
            }
        };
    }

    @Override
    //rownum
    public int getItemCount() {
        int size;
        if (mFavorites == null) {
            size = 0;
        } else {
            size = mFavorites.size();


        }
        return size;
    }

    @Override
    public Filter getFilter() {
        return favoritesFilter;
    }

    private Filter favoritesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FavoriteLocationObject> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mFavoritesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (FavoriteLocationObject w : mFavoritesFull) {
                    if (w.getLocationName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(w);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFavorites.clear();
            mFavorites.addAll((ArrayList<FavoriteLocationObject>) results.values);//TODO mfaviritesfull???
            notifyDataSetChanged();

        }
    };

    // stores and recycles views as they are scrolled off screen
    class WeatherViewHolder extends RecyclerView.ViewHolder {

        private final TextView weatherLocationItemView;
        private final TextView weatherDataItemView;
        private final ImageView iconImageView;
        private final TextView tempMaxTV;
        private final TextView tempMinTV;

        private WeatherViewHolder(final View itemView) {
            super(itemView);
            weatherLocationItemView = itemView.findViewById(R.id.locationTextView);
            weatherDataItemView = itemView.findViewById(R.id.dataTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            tempMaxTV = itemView.findViewById(R.id.maxTemperatureTV);
            tempMinTV = itemView.findViewById(R.id.minTemperatureTV);
        }
    }

    public static void changeIcon(Context mContext, ImageView iconImageView, String icon) {

        switch (icon) {
            case Icons.w01d:
                Glide.with(mContext).load(R.drawable.w01d).into(iconImageView);
                break;

            case Icons.w01n:
                Glide.with(mContext).load(R.drawable.w01n).into(iconImageView);
                break;


            case Icons.w02d:
                Glide.with(mContext).load(R.drawable.w02d).into(iconImageView);
                break;


            case Icons.w02n:
                Glide.with(mContext).load(R.drawable.w02n).into(iconImageView);
                break;


            case Icons.w03d:
            case Icons.w03n:
                Glide.with(mContext).load(R.drawable.w03d).into(iconImageView);
                break;


            case Icons.w04d:
            case Icons.w04n:
                Glide.with(mContext).load(R.drawable.w04d).into(iconImageView);
                break;


            case Icons.w09d:
            case Icons.w09n:
                Glide.with(mContext).load(R.drawable.w09d).into(iconImageView);
                break;


            case Icons.w10d:
                Glide.with(mContext).load(R.drawable.w10d).into(iconImageView);
                break;

            case Icons.w10n:
                Glide.with(mContext).load(R.drawable.w10n).into(iconImageView);
                break;


            case Icons.w11d:
            case Icons.w11n:
                Glide.with(mContext).load(R.drawable.w11d).into(iconImageView);
                break;

            case Icons.w13d:
            case Icons.w13n:
                Glide.with(mContext).load(R.drawable.w13d).into(iconImageView);
                break;

            case Icons.w50d:
            case Icons.w50n:
                Glide.with(mContext).load(R.drawable.w50d).into(iconImageView);
                break;

            default:
                Glide.with(mContext).load(R.drawable.www).into(iconImageView);
                break;

        }
    }

    private void launchLocationDetailsActivity(FavoriteLocationObject location, Context ctx) {
        Intent intent = new Intent(ctx, LocationDetailActivity.class);
        intent.putExtra(FAVORITE_LOCATION_OBJECT, location);
        ctx.startActivity(intent);
    }

}
