package com.example.weathertracking.ui.main.details;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weathertracking.adapters.WeatherDaysPagerAdapter;
import com.example.weathertracking.weatherApi.CurrentWeather;
import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.R;
import com.example.weathertracking.ui.main.MainActivity;
import com.example.weathertracking.sevicesAndReceiver.LocationService;
import com.example.weathertracking.models.Forecast;
import com.example.weathertracking.proba.HeaderView;
import com.example.weathertracking.weatherApi.weatherEffects.WeathersFromJson;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.luolc.emojirain.EmojiRainLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.weathertracking.Network.ConnectionStateMonitor.isConnectedToInternet;
import static com.example.weathertracking.sharedPrefAccess.CurrentLocation.getCurrentLocationFromSharedPref;
import static com.example.weathertracking.sharedPrefAccess.CurrentLocation.setLastCurrentLocation;
import static com.example.weathertracking.sharedPrefAccess.Favorites.addFavorite;
import static com.example.weathertracking.sharedPrefAccess.Favorites.checkIfIsFavorite;
import static com.example.weathertracking.sharedPrefAccess.Favorites.deleteFavorite;
import static com.example.weathertracking.sharedPrefAccess.Favorites.updateFavorite;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.CurrentWeatherCall.getCurrentWeatherByLatLng;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.WeatherForecastCalls.getForecastByLatlng;
import static com.example.weathertracking.weatherApi.weatherEffects.WeathersFromJson.getweathers;

public class LocationDetailFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    public static final String  FAVORITE_LOCATION_OBJECT = "favorite_location";
    public static final int FAVORITE_LOCATION_OBJECT_TYPE = -2;
    public static final int CURRENT_LOCATION_TYPE = -3;

    @BindView(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @BindView(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @BindView(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar)
    protected CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.emojiRainLayout)
    protected EmojiRainLayout emojiRainLayout;
    @BindView(R.id.scrollView)
    protected NestedScrollView scrollView;
    @BindView(R.id.weather_days_pager)
    protected ViewPager viewPager;

    @BindView(R.id.tab_layout_weather_days)
    protected TabLayout tabLayout;

    private boolean isHideToolbarView = false;
    private int fragmentType;
    private LatLng latLng;
    private Context mContext;

    private BroadcastReceiver networkActionReceiver;
    private BroadcastReceiver forecastReceiver;
    private BroadcastReceiver locationBroadcastReceiver;
    private BroadcastReceiver currentWeatherReceiver;
    private BroadcastReceiver animStartReceiver;

    private IntentFilter animFilter;
    private IntentFilter networkActionFilter;
    private IntentFilter cfilter;
    private IntentFilter weatherOkFilter;
    private IntentFilter locationFilter;
    private FavoriteLocationObject favoriteLocationObject;
    private Boolean registerNeeded = false;
    private View view;

    private int loading = 0;

    private boolean beenThereFromTop = false;
    private boolean beenThereFromBottom = false;
    private int isIsLocationGrantedOnPause = -1;

    private AnimationDrawable df;
    private AnimationDrawable dt;

    public LocationDetailFragment() {
        fragmentType = CURRENT_LOCATION_TYPE;

    }

    public LocationDetailFragment(FavoriteLocationObject favoriteLocationObject) {
        fragmentType = FAVORITE_LOCATION_OBJECT_TYPE;
        this.favoriteLocationObject = favoriteLocationObject;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initReceivers();
        //getweathers(mContext);
        if (isConnectedToInternet()) {
            if(fragmentType== FAVORITE_LOCATION_OBJECT_TYPE){
                view = inflater.inflate(R.layout.fragment_location_detail, container, false);
                loadPage();
            }else {
                if (getCurrentLocationFromSharedPref(mContext) == null && !MainActivity.isLocationGranted) {
                    view = inflater.inflate(R.layout.no_internet_fragment, container, false);
                    loadNoInternetPage("Please enable location");
                } else {
                    view = inflater.inflate(R.layout.fragment_location_detail, container, false);
                    loadPage();
                }
            }
        } else {
            if(fragmentType==CURRENT_LOCATION_TYPE) {
                if (getCurrentLocationFromSharedPref(mContext) == null) {
                    view = inflater.inflate(R.layout.no_internet_fragment, container, false);
                    loadNoInternetPage("No internet");
                }
                else {
                    view = inflater.inflate(R.layout.fragment_location_detail, container, false);
                    loadPage();
                }
            }
            else {
                view = inflater.inflate(R.layout.fragment_location_detail, container, false);
                loadPage();
            }
        }

        return view;
    }

    private void loadPage() {
        //Todo ha nincs net
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            //hogy ne csusszon a toolbar hatahoz
            @Override
            public void onScrollChanged() {
                scrollChanged();
            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Tomorrow"));
        tabLayout.addTab(tabLayout.newTab().setText("5days"));
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(animStartReceiver, animFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(networkActionReceiver, networkActionFilter);

        loadWeather();
        setRefreshAndFavorite();
        df = (AnimationDrawable) floatHeaderView.getRefreshIV().getBackground();
        dt = (AnimationDrawable) toolbarHeaderView.getRefreshIV().getBackground();
    }

    private void loadNoInternetPage(String error) {
        TextView errorTextView = view.findViewById(R.id.error_message);
        errorTextView.setText(error);
        errorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Fragment frg = null;
                frg = getFragmentManager().findFragmentByTag("Your_Fragment_TAG");
                final FragmentTransaction ft = getTargetFragment().bei;
                        getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();*/
            }
        });
    }

    private void startAnimation(int code,String icon) {//TODO ide is a weatherIdCheck
        String codeString;
        if(code<800){
            codeString=String.valueOf(code);
        }
        else {
            codeString= (code)+icon.substring(icon.length() - 1);
        }
        WeathersFromJson weathersFromJson = getweathers(mContext);
        setColor(weathersFromJson.getStartHex(codeString),weathersFromJson.getendHex(codeString));
        int rain =weathersFromJson.getRain(codeString);
        emojiRainLayout.stopDropping();
        if(rain>0){
            emojiRainLayout.addEmoji(R.drawable.drop);
            emojiRainLayout.setPer(rain);//ezt modositani
            emojiRainLayout.setDropDuration(5000);
            emojiRainLayout.setDropFrequency(500);
            emojiRainLayout.startDropping();
        }
        int snow =weathersFromJson.getSnow(codeString);
        if(snow>0){
            emojiRainLayout.addEmoji(R.drawable.snowflake_gray);
            emojiRainLayout.setPer(snow);//ezt modositani
            emojiRainLayout.setDropDuration(5000);
            emojiRainLayout.setDropFrequency(500);
            emojiRainLayout.startDropping();
        }

    }

    private void setColor(int startHex, int endHex) {

        int[] colors = {startHex, endHex};

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);

        view.setBackground(gradientDrawable);

    }

    private void scrollChanged() {
        final int y = 0;
        int scrollY = scrollView.getScrollY();
        if (scrollY > y) {
            scrollView.smoothScrollTo(0, y);
        }
// manage scrolling from top to bottom
        if (scrollY > y) {
            if (!beenThereFromTop) {
                beenThereFromTop = true;
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, y);
                    }
                });
            }
        }
        if (scrollY < y && beenThereFromTop) {
            beenThereFromTop = false;
        }
// manage scrolling from bottom to top
        if (scrollY < y) {
            if (!beenThereFromBottom) {
                beenThereFromBottom = true;
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, y);
                    }
                });
            }
        }
        if (scrollY > y && beenThereFromBottom) {
            beenThereFromBottom = false;
        }
    }//todo kell e mind

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            floatHeaderView.setVisibility(View.INVISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.INVISIBLE);
            floatHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        }

    }

    private void refreshWeather() {
        emojiRainLayout.stopDropping();
        if (fragmentType==CURRENT_LOCATION_TYPE) {

            if (MainActivity.isLocationGranted) {
                getCurrentLocationAndForecast();
                Intent refreshLocationIntent = new Intent(getContext(), LocationService.class);
                refreshLocationIntent.putExtra("Command", "REFRESH_LOCATION");
                mContext.startService(refreshLocationIntent);
            } else {
                Toast.makeText(mContext, "please, enable location", Toast.LENGTH_SHORT);
                stopLoadingIcon();

            }
        } else {
            getCurrentWeather();
            getForecast();
            getForecastByLatlng(getActivity(), favoriteLocationObject.getLatLng(),fragmentType);
            getCurrentWeatherByLatLng(mContext, favoriteLocationObject.getLatLng(), fragmentType);
        }
    }

    private void loadWeather() {

        if (fragmentType==CURRENT_LOCATION_TYPE) {
            setLastCurrentLocationFromSharedpref();

            if (MainActivity.isLocationGranted&&isConnectedToInternet()) {
                getCurrentLocationAndForecast();//First time, the main activity start the location service
            }
        } else {
            if (favoriteLocationObject.getCurrentWeatherObject() == null) {
                getCurrentWeather();
            } else {
                setCurrentWeather();//TODO utana nezni
            }
            if (favoriteLocationObject.getForecastObject() == null) {
                getForecast();
            } else {
                setForecast();
            }
        }
    }

    private void setLastCurrentLocationFromSharedpref() {

        if (floatHeaderView.getRefreshIV() != null) {
            df = (AnimationDrawable) floatHeaderView.getRefreshIV().getBackground();
            df.start();
        }

        if (toolbarHeaderView.getRefreshIV() != null) {
            dt = (AnimationDrawable) toolbarHeaderView.getRefreshIV().getBackground();
            dt.start();
        }
        FavoriteLocationObject lastCurrentLocation = getCurrentLocationFromSharedPref(mContext);
        if (lastCurrentLocation != null) {
            favoriteLocationObject = new FavoriteLocationObject(lastCurrentLocation);
            if (favoriteLocationObject.getCurrentWeatherObject() != null) {
                setCurrentWeather();
            }
            if (lastCurrentLocation.getForecastObject() != null) {
                setForecast();
            }
            setFavoriteButton();
        }

    }

    private void setRefreshAndFavorite() {
        View.OnClickListener refresh = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isLocationGranted || fragmentType== FAVORITE_LOCATION_OBJECT_TYPE) {
                    df = (AnimationDrawable) floatHeaderView.getRefreshIV().getBackground();
                    df.start();
                    if (toolbarHeaderView.getRefreshIV() != null) {
                        dt = (AnimationDrawable) toolbarHeaderView.getRefreshIV().getBackground();
                        dt.start();
                    }
                    refreshWeather();
                } else {
                    Toast.makeText(mContext, "please enable location", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (floatHeaderView.getRefreshIV() != null) {
            floatHeaderView.getRefreshIV().setOnClickListener(refresh);
        }
        if (toolbarHeaderView.getRefreshIV() != null) {
            toolbarHeaderView.getRefreshIV().setOnClickListener(refresh);
        }
        if (floatHeaderView.getFavoriteIV() != null) {
            floatHeaderView.getFavoriteIV().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkIfIsFavorite(favoriteLocationObject, mContext)) {
                        deleteFavorite(favoriteLocationObject, mContext);
                        floatHeaderView.getFavoriteIV().setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    } else {
                        addFavorite(favoriteLocationObject, mContext);
                        floatHeaderView.getFavoriteIV().setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                }
            });
            setFavoriteButton();
        }
    }

    private void setCurrentWeather() {
        startAnimation(favoriteLocationObject.getCurrentWeatherObject().getWeatherId(),favoriteLocationObject.getIcon());
        appBarLayout.addOnOffsetChangedListener(this);
        floatHeaderView.bindTo(favoriteLocationObject.getLocationName(), favoriteLocationObject.getCurrentTemp() + " °C", favoriteLocationObject.getIcon(), favoriteLocationObject.getWeather(), favoriteLocationObject.getCurrentWeatherObject().getLastrefreshDate());
        toolbarHeaderView.bindTo(favoriteLocationObject.getLocationName(), favoriteLocationObject.getCurrentTemp() + " °C", favoriteLocationObject.getIcon());
    }

    private void getCurrentWeather() {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(currentWeatherReceiver, cfilter);

        getCurrentWeatherByLatLng(mContext, favoriteLocationObject.getLatLng(), fragmentType);

    }

    private void getForecast() {

        getForecastByLatlng(getActivity(), favoriteLocationObject.getLatLng(),fragmentType);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(forecastReceiver, weatherOkFilter);
        //todo current vagy standard
    }

    private void setForecast() {
        if (favoriteLocationObject.getLatLng() != null) {
            latLng = favoriteLocationObject.getLatLng();
        }
        if (df != null) {
            df.stop();
        }
        if (dt != null) {
            dt.stop();
        }
        if (favoriteLocationObject.getForecastObject() != null) {
            try {
                final WeatherDaysPagerAdapter adapter =
                        new WeatherDaysPagerAdapter(((FragmentActivity) getContext()).getSupportFragmentManager(),
                                tabLayout.getTabCount(),
                                favoriteLocationObject.getForecastObject().getToday(), favoriteLocationObject.getForecastObject().getTomorrow(), favoriteLocationObject.getForecastObject().getForecast()) {
                        };
                viewPager.setAdapter(adapter);

                viewPager.addOnPageChangeListener(new
                        TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("lOCATION_DETAIL_FRAGMENT", "null forecast object");
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void getCurrentLocationAndForecast() {//TODO nev
        LocalBroadcastManager.getInstance(mContext).registerReceiver(locationBroadcastReceiver, locationFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(forecastReceiver, weatherOkFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(currentWeatherReceiver, cfilter);
        loading = 0;
    }

    private void initReceivers() {

        //fragment in foreground event
        animFilter = new IntentFilter(getString(R.string.START_ANIMATION));
        animStartReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (fragmentType==CURRENT_LOCATION_TYPE) {
                    if (favoriteLocationObject == null) {
                        FavoriteLocationObject lastCurrent = getCurrentLocationFromSharedPref(mContext);
                        if (lastCurrent != null) {
                            startAnimation(lastCurrent.getCurrentWeatherObject().getWeatherId(),lastCurrent.getIcon());
                        }
                    } else {
                        if (favoriteLocationObject.getCurrentWeatherObject() != null) {
                            startAnimation(favoriteLocationObject.getCurrentWeatherObject().getWeatherId(),favoriteLocationObject.getIcon());
                        }
                    }
                }
                //else wait until icon is set
            }
        };

        //current weather refreshReceiver
        cfilter = new IntentFilter("CURRENT_WEATHER"+fragmentType);
        currentWeatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (("CURRENT_WEATHER"+fragmentType).equals(action)) {

                    CurrentWeather currentWeather = (CurrentWeather) intent.getSerializableExtra("CURRENT_WEATHER_OBJECT");
                    if (currentWeather != null) {
                        favoriteLocationObject.setLocationName(currentWeather.getLocationName());//TODO ez igy fura
                        favoriteLocationObject.setCurrentWeather(currentWeather);
                        if (fragmentType==CURRENT_LOCATION_TYPE) {
                            setLastCurrentLocation(favoriteLocationObject, mContext);
                        } else {
                            updateFavorite(favoriteLocationObject, mContext);
                        }
                        setCurrentWeather();
                    } else {
                        Log.e("location detail fragment", "null weather object");
                    }
                    LocalBroadcastManager.getInstance(mContext).unregisterReceiver(currentWeatherReceiver);

                }
                stopLoadingIcon();
            }
        };


        //forecast refreshReceiver filter
        weatherOkFilter = new IntentFilter("WEATHER_OK"+fragmentType);
        forecastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (("WEATHER_OK"+fragmentType).equals(action)) {

                    Forecast forecast = (Forecast) intent.getSerializableExtra("FORECAST_OBJECT");
                    favoriteLocationObject.setForecast(forecast);
                    setFavoriteButton();
                    setForecast();
                    if (fragmentType==CURRENT_LOCATION_TYPE) {
                        setLastCurrentLocation(favoriteLocationObject, mContext);
                    } else {
                        updateFavorite(favoriteLocationObject, mContext);
                    }
                }
                stopLoadingIcon();
                Log.d("Location**", " LocationDetailFragment weather refreshed");
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(forecastReceiver);

            }

        };

        //current location refreshReceiver
        locationFilter = new IntentFilter(MainActivity.LOCATION_KEY);
        locationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(mContext, "Current location refreshed!", Toast.LENGTH_SHORT).show();
                latLng = new LatLng((double) intent.getExtras().get("Latitude"), (double) intent.getExtras().get("Longitude"));
                getCurrentWeatherByLatLng(mContext, latLng, fragmentType);
                getForecastByLatlng(mContext, latLng,fragmentType);
                favoriteLocationObject = new FavoriteLocationObject(latLng);
                Log.d("Location*", " LocationDetailFragment current location received");
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(locationBroadcastReceiver);
            }
        };

        //Network action refreshReceiver
        networkActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("DISCONNECTED".equals(intent.getStringExtra("TYPE"))) {

                    hideRefresh();
                } else {
                    if ("RECONNECTED".equals(intent.getStringExtra("TYPE"))) {
                        if (toolbarHeaderView.getRefreshIV() != null) {
                            toolbarHeaderView.getRefreshIV().setVisibility(View.VISIBLE);
                        }
                        if (floatHeaderView.getRefreshIV() != null) {
                            floatHeaderView.getRefreshIV().setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        networkActionFilter = new IntentFilter("NETWORK_ACTION");
    }

    private void setFavoriteButton() {
        if (checkIfIsFavorite(favoriteLocationObject, mContext)) {
            if (floatHeaderView.getFavoriteIV() != null) {
                floatHeaderView.getFavoriteIV().setImageResource(R.drawable.ic_favorite_black_24dp);
            }
        } else {

            if (floatHeaderView.getFavoriteIV() != null) {
                floatHeaderView.getFavoriteIV().setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }
    }

    private void stopLoadingIcon() {//TODO rendes nevet nekije
        loading++;
        if (loading == 2) {
            setLastCurrentLocation(favoriteLocationObject, mContext);
            if (df != null) {
                df.stop();
            }
            if (dt != null) {
                dt.stop();
            }
            loading = 0;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (registerNeeded) {
            if (MainActivity.isLocationGranted) {
                LocalBroadcastManager.getInstance(mContext).registerReceiver(locationBroadcastReceiver, locationFilter);
                LocalBroadcastManager.getInstance(mContext).registerReceiver(currentWeatherReceiver, cfilter);
                LocalBroadcastManager.getInstance(mContext).registerReceiver(forecastReceiver, weatherOkFilter);
                LocalBroadcastManager.getInstance(mContext).registerReceiver(networkActionReceiver, networkActionFilter);
                registerNeeded = false;
            }
        }
        if (!isConnectedToInternet()) {
            Toast.makeText(mContext, "No internet", Toast.LENGTH_LONG).show();
            hideRefresh();
        }

        if (MainActivity.isLocationGranted && isIsLocationGrantedOnPause == 0) {
            isIsLocationGrantedOnPause = 1;
            getActivity().recreate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(locationBroadcastReceiver);

        try {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(currentWeatherReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(forecastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(animStartReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerNeeded = true;
        if (MainActivity.isLocationGranted) {
            isIsLocationGrantedOnPause = 1;
        } else {
            isIsLocationGrantedOnPause = 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(networkActionReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideRefresh() {
        if (toolbarHeaderView != null) {
            if (toolbarHeaderView.getRefreshIV() != null) {
                toolbarHeaderView.getRefreshIV().setVisibility(View.INVISIBLE);
            }
        }
        if (floatHeaderView != null) {
            if (floatHeaderView.getRefreshIV() != null) {
                floatHeaderView.getRefreshIV().setVisibility(View.INVISIBLE);
            }
        }
    }
}
