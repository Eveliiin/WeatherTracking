package com.example.weathertracking.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.telephony.AvailableNetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weathertracking.Adapters.SearchResultAdapter;
import com.example.weathertracking.Models.CurrentWeather;
import com.example.weathertracking.Models.FavoriteLocationObject;
import com.example.weathertracking.R;
import com.example.weathertracking.Services.LocationService;
import com.example.weathertracking.Utils.WeatherService;
import com.example.weathertracking.proba.HeaderView;
import com.example.weathertracking.weatherApi.SearchCityObject;
import com.example.weathertracking.weatherApi.SearchCityCallResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.weathertracking.Activities.MainActivity.LOCATION_KEY;
import static com.example.weathertracking.Fragments.LocationDetailFragment.FAVORITE_LOCATION_OBJECT;
import static com.example.weathertracking.Utils.Favorites.checkIfIsFavorite;
import static com.example.weathertracking.Utils.Favorites.modifyFavorite;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.CurrentWeatherCall.LOCATION_SEARCH_A;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.CurrentWeatherCall.getCurrentWeatherByLatLng;
import static com.example.weathertracking.weatherApi.WeatherCallMembers.AppId;
import static com.example.weathertracking.weatherApi.WeatherCallMembers.BaseUrl;

public class LocationSearchActivity extends AppCompatActivity {

    @BindView(R.id.searchLocationsButton)
    protected Button searchLocationsButton;

    @BindView(R.id.viewWeatherButton)
    protected Button viewWeatherButton;

    @BindView(R.id.favoriteSwitch)
    protected Switch favoriteSwitch;

    @BindView(R.id.resultsRecyclerView)
    protected RecyclerView recyclerView;

    @BindView(R.id.noResultsTextView)
    protected TextView noResultsTextView;

    @BindView(R.id.locationTextView)
    protected TextView locationEditText;

    @BindView(R.id.locationImage)
    protected ImageButton locationSearch;
    List<SearchCityObject> results;

    private SearchResultAdapter adapter;
    private String Provider;
    private LatLng currentLatLon;

    //WeatherForecastCalls forecastObject;
    private FavoriteLocationObject favoriteLocationObject;
    static int NO_ITEM_SELECTED=-1;
    int selecteditemNum= -1;

    private LatLng selectedLocationCoord;
    private String selectedLocationName;

    private ArrayList<FavoriteLocationObject> locationsForMap;
    private CompoundButton.OnCheckedChangeListener switchListener;
    private BroadcastReceiver itemClickedReceiver;

    private BroadcastReceiver choosedLocationReceiver;
    private BroadcastReceiver locationReceiver;
    private BroadcastReceiver currentWeatherReceiver;
    private BroadcastReceiver searchCitiesReceiver;
    private BroadcastReceiver networkActionReceiver;

    private IntentFilter currentWeatherFilter;
    private IntentFilter searchCitiesFilter;
    private IntentFilter intentFilterLocation;
    private IntentFilter networkActionFilter;
    private boolean isNetwork=true;
    private boolean currentLocationIsUsed=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        ButterKnife.bind(this);
        viewWeatherButton.setVisibility(View.INVISIBLE);
        selecteditemNum=NO_ITEM_SELECTED;
        switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modifyFavorite(locationsForMap.get(selecteditemNum),getBaseContext());
            }
        };

        favoriteSwitch.setOnCheckedChangeListener(switchListener);

        locationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!currentLocationIsUsed) {
                    searchLocation(s);
                }
                else {
                    //TODO ha nem egyenlo a currentlocationnal, keressen
                    currentLocationIsUsed=false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotation_animation);
                locationSearch.startAnimation(aniRotate);
                currentLocationIsUsed=true;
                clearRecyclerView();
                noResultsTextView.setVisibility(View.INVISIBLE);

                Intent refreshLocationIntent = new Intent(LocationSearchActivity.this, LocationService.class);
                refreshLocationIntent.putExtra("Command", "REFRESH_LOCATION");
                startService(refreshLocationIntent);

                locationReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Provider = intent.getStringExtra("Provider");
                        currentLatLon=new LatLng((double)intent.getExtras().get("Latitude"),(double)intent.getExtras().get("Longitude"));
                        favoriteLocationObject = new FavoriteLocationObject(currentLatLon);
                        Toast.makeText(getApplicationContext(), "Current location is set!", Toast.LENGTH_SHORT).show();
                        setCurrentWeatherReceiver();
                        try {
                            unregisterReceiver(locationReceiver);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(LOCATION_KEY);
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(locationReceiver, intentFilter);
            }
        });
        viewWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(LocationSearchActivity.this, LocationDetailActivity.class);
                if(favoriteLocationObject!=null){
                    //current location
                    i.putExtra(FAVORITE_LOCATION_OBJECT,favoriteLocationObject);
                }else {// TODO terkep hosszan??
                    //from list
                    i.putExtra(FAVORITE_LOCATION_OBJECT,new FavoriteLocationObject(selectedLocationCoord));
                }
                startActivity(i);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        networkActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if("DISCONNECTED".equals(intent.getStringExtra("TYPE"))){
                    if(recyclerView.getAdapter()!=null){
                        Toast.makeText(context, "no internet, select a location form list or reconnect to the internet", Toast.LENGTH_LONG).show();//TODO
                        viewWeatherButton.setVisibility(View.INVISIBLE);
                        locationSearch.setVisibility(View.INVISIBLE);
                        searchLocationsButton.setVisibility(View.INVISIBLE);
                        locationEditText.setEnabled(false);
                        isNetwork =false;
                    }
                    else {
                        Toast.makeText(context, "no internet, return to the main page", Toast.LENGTH_LONG).show();//TODO
                        unregisterReceiver(networkActionReceiver);
                        finish();

                    }
                }
                else {
                    if("RECONNECTED".equals(intent.getStringExtra("TYPE"))){
                        Toast.makeText(context, "reconnected", Toast.LENGTH_LONG).show();//TODO
                        locationSearch.setVisibility(View.VISIBLE);
                        locationEditText.setEnabled(true);
                        if(selecteditemNum!=NO_ITEM_SELECTED){
                            viewWeatherButton.setVisibility(View.VISIBLE);
                        }
                        if(recyclerView.getAdapter()!=null){
                            searchLocationsButton.setVisibility(View.VISIBLE);
                        }
                        isNetwork =true;
                    }
                }
            }
        };
        networkActionFilter = new IntentFilter("NETWORK_ACTION");
        registerReceiver(networkActionReceiver,networkActionFilter);
    }
    void searchLocation(CharSequence s){
        viewWeatherButton.setVisibility(View.INVISIBLE);//kell e ide if vagy nem>

        favoriteSwitch.setChecked(false);
        favoriteSwitch.setClickable(false);
        if (currentLocationIsUsed) {

            if (!s.equals(favoriteLocationObject.getLocationName())) {
                locationEditText.setTextColor(Color.BLACK);

                currentLocationIsUsed = false;
                if (s.toString().length() == 0) {
                    noResultsTextView.setVisibility(View.INVISIBLE);
                    if (recyclerView.getHeight() > 0) {
                        adapter = new SearchResultAdapter(getBaseContext(), new ArrayList<SearchCityObject>());
                    }
                } else {
                    if (s.toString().length() < 3) {
                        noResultsTextView.setText("Location name is too short!");
                        noResultsTextView.setVisibility(View.VISIBLE);
                        clearRecyclerView();
                    } else {
                        String searchString = locationEditText.getText().toString();
                        searchLocation(searchString);
                    }
                }
            }
        } else {
            if (s.toString().length() == 0) {
                noResultsTextView.setVisibility(View.INVISIBLE);
                if (recyclerView.getHeight() > 0) {
                    adapter = new SearchResultAdapter(getBaseContext(), new ArrayList<SearchCityObject>());
                }
            } else {
                if (s.toString().length() < 3) {
                    noResultsTextView.setText("Location name is too short!");
                    noResultsTextView.setVisibility(View.VISIBLE);
                    clearRecyclerView();
                } else {
                    String searchString = locationEditText.getText().toString();
                    searchLocation(searchString);
                }

            }
        }
    }

    void selectItemInResultsList(int i){

        if(selecteditemNum!= i) {

            if(selecteditemNum!=NO_ITEM_SELECTED){//TODO
                recyclerView.getChildAt(selecteditemNum).setBackgroundColor(Color.parseColor("#ADD8E6"));
            }
            selecteditemNum = i;
            recyclerView.getChildAt(i).setBackgroundColor(Color.CYAN);
            selectedLocationCoord=locationsForMap.get(i).getLatLng();
            selectedLocationName=locationsForMap.get(i).getLocationName();
            if(isNetwork){
                viewWeatherButton.setVisibility(View.VISIBLE);
            }
            favoriteCheck(locationsForMap.get(selecteditemNum));

            Toast.makeText(getApplicationContext(), selectedLocationName + " selected!", Toast.LENGTH_SHORT).show();
        }
        else{
            viewWeatherButton.setVisibility(View.INVISIBLE);
            favoriteSwitch.setOnCheckedChangeListener(null);
            favoriteSwitch.setChecked(false);
            favoriteSwitch.setOnCheckedChangeListener(switchListener);
            favoriteSwitch.setClickable(false);
            //Toast.makeText(getApplicationContext(), selectedLocationName + " deselected!", Toast.LENGTH_SHORT).show();
            recyclerView.getChildAt(selecteditemNum).setBackgroundColor(Color.parseColor("#ADD8E6"));
            selectedLocationCoord = null;
            selectedLocationName = null;
            selecteditemNum=NO_ITEM_SELECTED;
        }
    }
    void favoriteCheck(FavoriteLocationObject favoriteLocation){
        if(checkIfIsFavorite(favoriteLocation,getBaseContext())){
            favoriteSwitch.setOnCheckedChangeListener(null);
            favoriteSwitch.setChecked(true);
            favoriteSwitch.setOnCheckedChangeListener(switchListener);
        }else {favoriteSwitch.setOnCheckedChangeListener(null);
            favoriteSwitch.setChecked(false);
            favoriteSwitch.setOnCheckedChangeListener(switchListener);
        }
        favoriteSwitch.setClickable(true);//TODO kitenni fuggsenybe
    }

    private void setCurrentWeatherReceiver(){
        //current weather receiver
        currentWeatherFilter = new IntentFilter("CURRENT_WEATHER_SEARCH_A");
        currentWeatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("CURRENT_WEATHER".equals(action)) {

                    CurrentWeather currentWeather =(CurrentWeather) intent.getSerializableExtra("CURRENT_WEATHER_OBJECT");
                    favoriteLocationObject.setCurrentWeather(currentWeather);
                    //azert igy, mert ha a setcurrentweatherbe teszem bele a setLocationNamet, osszekavarja a favoritesban
                    favoriteLocationObject.setLocationName(currentWeather.getLocationName());
                    locationEditText.setTextColor(Color.GREEN);

                    locationEditText.setText(favoriteLocationObject.getLocationName());
                    locationSearch.clearAnimation();
                    viewWeatherButton.setVisibility(View.VISIBLE);
                    try {
                        context.unregisterReceiver(currentWeatherReceiver);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        registerReceiver(currentWeatherReceiver, currentWeatherFilter);
        getCurrentWeatherByLatLng(getApplicationContext(),favoriteLocationObject.getLatLng(),LOCATION_SEARCH_A);

    }
    void clearRecyclerView(){
        adapter = new SearchResultAdapter(getBaseContext(), new ArrayList<SearchCityObject>());
        recyclerView.setAdapter(adapter);
        searchLocationsButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(itemClickedReceiver);//TODO nem jo ide

    }

    @Override
    protected void onResume() {
        super.onResume();
        itemClickedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int selectedLocationPosition= intent.getIntExtra("ITEM",0);
                selectItemInResultsList(selectedLocationPosition);

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LIST_ITEM_CLICKED");
        registerReceiver(itemClickedReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void searchLocation(String searchString) {//TODO rendes nevet a valtozonak
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
                    final ArrayList<SearchCityObject> results = new ArrayList<>(weatherForecastResult.list);
                    recyclerView.removeAllViews();
                    ////
                    adapter = new SearchResultAdapter(getBaseContext(), results);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setHasFixedSize(true);
                    if (results.size() > 0) {
                        searchLocationsButton.setVisibility(View.VISIBLE);
                        if (noResultsTextView.getVisibility() == View.VISIBLE) {
                            noResultsTextView.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        noResultsTextView.setText("No result");
                        noResultsTextView.setVisibility(View.VISIBLE);

                    }
                    locationsForMap = new ArrayList<FavoriteLocationObject>() {
                    };
                    for (SearchCityObject m : results) {
                        locationsForMap.add(new FavoriteLocationObject(m.name, new LatLng(m.coord.lat,m.coord.lon)));
                    }

                    searchLocationsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LocationSearchActivity.this, MapsActivity.class);
                            intent.putExtra("CHOOSED",selecteditemNum);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list", locationsForMap);
                            intent.putExtras(bundle);

                            choosedLocationReceiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    selectedLocationCoord= intent.getParcelableExtra("COORDINATES");
                                    selectedLocationName= intent.getStringExtra("NAME");

                                    if(selectedLocationCoord!=null) {
                                        for (int i=0;i<results.size();i++)
                                        {
                                            if(results.get(i).coord.lat==selectedLocationCoord.latitude && results.get(i).coord.lon==selectedLocationCoord.longitude){
                                                selectItemInResultsList(i);
                                            }
                                        }

                                    }
                                    unregisterReceiver(choosedLocationReceiver);

                                }
                            };
                            intentFilterLocation = new IntentFilter();
                            intentFilterLocation.addAction("CHOOSED_LOCATION");
                            registerReceiver(choosedLocationReceiver,intentFilterLocation);
                            startActivity(intent);//TODO startActivityForResult??
                        }
                    });
                }
            }


            @Override
            public void onFailure(@NonNull Call<SearchCityCallResult> call, @NonNull Throwable t) {
            }
        });
    }
}