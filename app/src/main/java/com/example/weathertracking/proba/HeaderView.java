package com.example.weathertracking.proba;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.weathertracking.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.weathertracking.adapters.FavoriteListAdapter.changeIcon;

public class HeaderView extends CoordinatorLayout {

    @BindView(R.id.currentWeatherImageView)
    ImageView mWeatherIcon;

    @BindView(R.id.locationTextView)
    TextView locationNameTV;


    @Nullable
    @BindView(R.id.refreshImageView)
    ImageView refreshIV;

    @BindView(R.id.currentDegreeTV)
    TextView currentDegreeTV;

    @Nullable
    @BindView(R.id.currentWeather)
    TextView currentWeatherTV;

    @Nullable
    @BindView(R.id.lastRefreshDateTextView)
    TextView lastRefreshDateTextViewTV;

    @Nullable
    @BindView(R.id.favorite)
    ImageView favoriteIB;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Nullable
    public ImageView getRefreshIV() {
        return refreshIV;
    }

    @Nullable
    public ImageView getFavoriteIB() {
        return favoriteIB;
    }

    public TextView getLocationNameTV() {
        return locationNameTV;
    }

    public void bindTo(String  location, String currentDegree, String icon, String currentWeather, String lastRDate) {
        this.locationNameTV.setText(location);
        changeIcon(getContext(), this.mWeatherIcon, icon);
        this.currentDegreeTV.setText(currentDegree);
        //this.refreshIV=refreshIV;
        this.currentWeatherTV.setText(currentWeather);
        this.lastRefreshDateTextViewTV.setText(lastRDate);
        //this.fav?
    }

    public void bindTo(String  location, String currentDegree, String icon) {
        this.locationNameTV.setText(location);
        changeIcon(getContext(), this.mWeatherIcon, icon);
        this.currentDegreeTV.setText(currentDegree);
        //this.refreshIV=refreshIV;
    }



    public void setTextSize(float size) {
        locationNameTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

}