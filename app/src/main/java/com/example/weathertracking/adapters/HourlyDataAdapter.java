package com.example.weathertracking.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weathertracking.models.HourlyWeather;
import com.example.weathertracking.R;
import com.example.weathertracking.weatherApi.WeatherForecastObject;
import com.example.weathertracking.Utils.Icons;

import java.util.ArrayList;
import java.util.List;

public class HourlyDataAdapter extends RecyclerView.Adapter<com.example.weathertracking.adapters.HourlyDataAdapter.WeatherViewHolder> {

    private List<HourlyWeather> mWeathers= new ArrayList<>();

    private final LayoutInflater mInflater;
    Context mContext;

    public HourlyDataAdapter(Context context, List<WeatherForecastObject> weathers) {
        this.mInflater = LayoutInflater.from(context);
        mContext=context;

        if(weathers !=null){
            for(int i=0;i<weathers.size();i++){
                WeatherForecastObject w = weathers.get(i);
                HourlyWeather hourlyWeatherNew = new HourlyWeather(w.dt_txt,String.valueOf(w.main.temp),
                        String.valueOf(w.main.feels_like),w.weather.get(0).icon,w.weather.get(0).description,
                        String.valueOf(w.wind.speed),String.valueOf(w.main.humidity),
                        String.valueOf(w.main.pressure));
                mWeathers.add(hourlyWeatherNew);
            }
        }
        else {
            Log.e("hourly data adatper","null weathers array");
        }

    }
   /* public HourlyDataAdapter(Context context,List<HourlyWeather> weathers,int i){
        this.mInflater = LayoutInflater.from(context);
        this.mWeathers=weathers;
    }*/

    @NonNull
    @Override
    // inflates the row layout from xml when needed
    public com.example.weathertracking.adapters.HourlyDataAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.weather_item_hourly, parent, false);
        return new com.example.weathertracking.adapters.HourlyDataAdapter.WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.weathertracking.adapters.HourlyDataAdapter.WeatherViewHolder holder, int position) {
        HourlyWeather w = mWeathers.get(position);

        String temperatureText=(Math.round(Float.parseFloat(w.getTemperature())) - 272) +"°C";
        String feelsLikeText = (Math.round(Float.parseFloat(w.getFeelsLike()) - 272))+"°C";

        holder.dataTextView.setText(w.getWeatherData());
        holder.dateTextView.setText(w.getData());
        holder.temperatureTextView.setText(temperatureText);
        holder.temperatureFeelsLikeTextView.setText(feelsLikeText);
        holder.windTextView.setText(w.getWind());
        holder.humidityTextView.setText(w.getHumidity());
        holder.pressureTextView.setText(w.getPressure());

        String icon = w.getIcon();

        switch (icon)
        {
            case Icons.w01d:
                Glide.with(mContext).load(R.drawable.w01d).into(holder.iconImageView);
                break;

            case Icons.w01n:
                Glide.with(mContext).load(R.drawable.w01n).into(holder.iconImageView);
                break;


            case  Icons.w02d:
                Glide.with(mContext).load(R.drawable.w02d).into(holder.iconImageView);
                break;


            case Icons.w02n:
                Glide.with(mContext).load(R.drawable.w02n).into(holder.iconImageView);
                break;


            case Icons.w03d:
            case Icons.w03n:
                Glide.with(mContext).load(R.drawable.w03d).into(holder.iconImageView);
                break;


            case Icons.w04d:
            case Icons.w04n:
                Glide.with(mContext).load(R.drawable.w04d).into(holder.iconImageView);
                break;


            case Icons.w09d:
            case Icons.w09n:
                Glide.with(mContext).load(R.drawable.w09d).into(holder.iconImageView);
                break;



            case Icons.w10d:
                Glide.with(mContext).load(R.drawable.w10d).into(holder.iconImageView);
                break;

            case Icons.w10n:
                Glide.with(mContext).load(R.drawable.w10n).into(holder.iconImageView);
                break;



            case Icons.w11d:
            case Icons.w11n:
                Glide.with(mContext).load(R.drawable.w11d).into(holder.iconImageView);
                break;

            case Icons.w13d:
            case Icons.w13n:
                Glide.with(mContext).load(R.drawable.w13d).into(holder.iconImageView);
                break;

            case Icons.w50d:
            case Icons.w50n:
                Glide.with(mContext).load(R.drawable.w50d).into(holder.iconImageView);
                break;

            default:
                Glide.with(mContext).load(R.drawable.www).into(holder.iconImageView);
                break;

        }

    }

    public void setWeather(List<HourlyWeather> weathers) {
        mWeathers = weathers;
        notifyDataSetChanged();
    }

    @Override
    //rownum
    public int getItemCount() {
        return mWeathers.size();
    }


    // stores and recycles views as they are scrolled off screen
    class WeatherViewHolder extends RecyclerView.ViewHolder {


        private final TextView dateTextView;
        private final TextView temperatureTextView;
        private final TextView temperatureFeelsLikeTextView;
        private final ImageView iconImageView;
        private final TextView dataTextView;
        private final TextView windTextView;
        private final TextView humidityTextView;
        private final TextView pressureTextView;

        private WeatherViewHolder(final View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            temperatureFeelsLikeTextView = itemView.findViewById(R.id.temperatureFeelsLikeTextView);
            iconImageView = itemView.findViewById(R.id.icon);
            dataTextView = itemView.findViewById(R.id.dataTextView);
            windTextView = itemView.findViewById(R.id.windTextView);
            humidityTextView = itemView.findViewById(R.id.humidityTextView);
            pressureTextView = itemView.findViewById(R.id.pressureTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //clickListener.onItemClick(view, getAdapterPosition());
                    Log.d("Element", "Clicked -" + dataTextView.getText());

                }
            });
        }
    }


}
