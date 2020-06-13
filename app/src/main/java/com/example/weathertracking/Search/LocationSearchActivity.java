package com.example.weathertracking.Search;

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
import android.os.Bundle;
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

import com.example.weathertracking.adapters.SearchResultAdapter;
import com.example.weathertracking.main.LocationDetailActivity;
import com.example.weathertracking.models.CurrentWeather;
import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.R;
import com.example.weathertracking.sevicesAndReceiver.LocationService;
import com.example.weathertracking.weatherApi.WeatherService;
import com.example.weathertracking.weatherApi.SearchCityObject;
import com.example.weathertracking.weatherApi.SearchCityCallResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.weathertracking.main.MainActivity.LOCATION_KEY;
import static com.example.weathertracking.main.details.LocationDetailFragment.FAVORITE_LOCATION_OBJECT;
import static com.example.weathertracking.sharedPrefAccess.Favorites.checkIfIsFavorite;
import static com.example.weathertracking.sharedPrefAccess.Favorites.modifyFavorite;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.CurrentWeatherCall.LOCATION_SEARCH_A;
import static com.example.weathertracking.weatherApi.WeatherApiCalls.CurrentWeatherCall.getCurrentWeatherByLatLng;
import static com.example.weathertracking.weatherApi.WeatherCallMembers.AppId;
import static com.example.weathertracking.weatherApi.WeatherCallMembers.BaseUrl;

public class LocationSearchActivity extends AppCompatActivity {

    static int NO_ITEM_SELECTED = -1;
    int selecteditemNum = -1;

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
    protected ImageButton currentLocationButton;

    private SearchResultAdapter adapter;
    private ArrayList<SearchCityObject> results;

    private ArrayList<FavoriteLocationObject> locationsForMap;
    private FavoriteLocationObject currentLocationObject;

    private FavoriteLocationObject selectedLocationObject;

    private BroadcastReceiver itemClickedReceiver;
    private BroadcastReceiver choosedLocationReceiver;
    private BroadcastReceiver locationReceiver;
    private BroadcastReceiver currentWeatherReceiver;
    private BroadcastReceiver networkActionReceiver;

    private IntentFilter intentFilter;
    private IntentFilter currentWeatherFilter;
    private IntentFilter intentFilterLocation;
    private IntentFilter networkActionFilter;

    private CompoundButton.OnCheckedChangeListener switchListener;

    private boolean isNetwork = true;
    private boolean currentLocationIsUsed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SearchResultAdapter(getBaseContext());
        setContentView(R.layout.activity_location_search);
        ButterKnife.bind(this);
        viewWeatherButton.setVisibility(View.INVISIBLE);
        selecteditemNum = NO_ITEM_SELECTED;
        switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modifyFavorite(locationsForMap.get(selecteditemNum), getBaseContext());
            }
        };

        favoriteSwitch.setOnCheckedChangeListener(switchListener);

        locationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() < 4 && s.length() != 0) {
                    tooShortCase();
                } else {
                    search(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLocationButton.setClickable(false);
                locationEditText.setEnabled(false);
                locationEditText.setText("");
                currentLocationIsUsed = true;
                Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation_animation);
                currentLocationButton.startAnimation(aniRotate);
                hideFeatures();
                Intent refreshLocationIntent = new Intent(LocationSearchActivity.this, LocationService.class);
                refreshLocationIntent.putExtra(getString(R.string.COMMAND), getString(R.string.REFRESH_LOCATION));
                startService(refreshLocationIntent);
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(locationReceiver, intentFilter);
            }
        });
        viewWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LocationSearchActivity.this, LocationDetailActivity.class);
                if (currentLocationObject != null) {//current location
                    i.putExtra(FAVORITE_LOCATION_OBJECT, currentLocationObject);
                } else {// TODO terkep hosszan??//from list
                    i.putExtra(FAVORITE_LOCATION_OBJECT, selectedLocationObject);
                }
                startActivity(i);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        initReceivers();
        registerReceiver(networkActionReceiver, networkActionFilter);
    }

    void search(CharSequence s) {
        if (!currentLocationIsUsed) {
            viewWeatherButton.setVisibility(View.INVISIBLE);//kell e ide if vagy nem>

            favoriteSwitch.setChecked(false);
            favoriteSwitch.setClickable(false);
            if (currentLocationObject != null) {
                if (currentLocationObject.getLocationName() != s) {
                    locationEditText.setTextColor(Color.BLACK);
                    currentLocationObject = null;
                    searchLocation(s.toString());
                }
            } else {
                searchLocation(s.toString());
            }
        } else {
            currentLocationIsUsed = false;
        }

    }

    void tooShortCase() {
        hideFeatures();
        noResultsTextView.setText(R.string.location_name_too_short);
        noResultsTextView.setVisibility(View.VISIBLE);
    }

    void hideFeatures() {
        clearRecyclerView();
        noResultsTextView.setVisibility(View.INVISIBLE);
        viewWeatherButton.setVisibility(View.INVISIBLE);

    }

    void selectItemInResultsList(int i) {

        if (selecteditemNum != i) {

            if (selecteditemNum != NO_ITEM_SELECTED) {//TODO
                recyclerView.getChildAt(selecteditemNum).setBackgroundColor(Color.parseColor("#ADD8E6"));
            }
            selecteditemNum = i;
            recyclerView.getChildAt(i).setBackgroundColor(Color.CYAN);
            selectedLocationObject = new FavoriteLocationObject(locationsForMap.get(i));
            if (isNetwork) {
                viewWeatherButton.setVisibility(View.VISIBLE);
            }
            favoriteCheck(locationsForMap.get(selecteditemNum));

            Toast.makeText(getApplicationContext(), selectedLocationObject.getLocationName() + " selected!", Toast.LENGTH_SHORT).show();
        } else {
            viewWeatherButton.setVisibility(View.INVISIBLE);
            favoriteSwitch.setOnCheckedChangeListener(null);
            favoriteSwitch.setChecked(false);
            favoriteSwitch.setOnCheckedChangeListener(switchListener);
            favoriteSwitch.setClickable(false);
            //Toast.makeText(getApplicationContext(), sele + " deselected!", Toast.LENGTH_SHORT).show();
            recyclerView.getChildAt(selecteditemNum).setBackgroundColor(Color.parseColor("#ADD8E6"));
            selectedLocationObject = null;
            selecteditemNum = NO_ITEM_SELECTED;
        }
    }

    void favoriteCheck(FavoriteLocationObject favoriteLocation) {
        if (checkIfIsFavorite(favoriteLocation, getBaseContext())) {
            favoriteSwitch.setOnCheckedChangeListener(null);
            favoriteSwitch.setChecked(true);
            favoriteSwitch.setOnCheckedChangeListener(switchListener);
        } else {
            favoriteSwitch.setOnCheckedChangeListener(null);
            favoriteSwitch.setChecked(false);
            favoriteSwitch.setOnCheckedChangeListener(switchListener);
        }
        favoriteSwitch.setClickable(true);//TODO kitenni fuggsenybe
    }

    private void setCurrentWeatherReceiver() {
        //current weather receiver

        registerReceiver(currentWeatherReceiver, currentWeatherFilter);
        getCurrentWeatherByLatLng(getApplicationContext(), currentLocationObject.getLatLng(), LOCATION_SEARCH_A);
    }

    void clearRecyclerView() {
        if (adapter != null) {
            adapter.deleteElements();
            recyclerView.setAdapter(adapter);
        }
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
                int selectedLocationPosition = intent.getIntExtra("ITEM", 0);
                selectItemInResultsList(selectedLocationPosition);

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LIST_ITEM_CLICKED");
        registerReceiver(itemClickedReceiver, intentFilter);
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
                    results = new ArrayList<>(weatherForecastResult.list);
                    recyclerView.removeAllViews();
                    ////
                    adapter.setResults(results);
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
                        locationsForMap.add(new FavoriteLocationObject(m.name, new LatLng(m.coord.lat, m.coord.lon)));
                    }

                    searchLocationsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LocationSearchActivity.this, MapsActivity.class);
                            intent.putExtra("CHOOSED", selecteditemNum);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("list", locationsForMap);
                            intent.putExtras(bundle);

                            registerReceiver(choosedLocationReceiver, intentFilterLocation);
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

    private void initReceivers() {

        //CurrentWeather, needed for locationName, and view weather option
        currentWeatherFilter = new IntentFilter(getString(R.string.CURRENT_WEATHER_SEARCH_A));
        currentWeatherReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (getString(R.string.CURRENT_WEATHER_SEARCH_A).equals(action)) {

                    CurrentWeather currentWeather = (CurrentWeather) intent.getSerializableExtra(getString(R.string.CURRENT_WEATHER_OBJECT));
                    currentLocationObject.setCurrentWeather(currentWeather);
                    //azert igy, mert ha a setcurrentweatherbe teszem bele a setLocationNamet, osszekavarja a favoritesban
                    currentLocationObject.setLocationName(currentWeather.getLocationName());
                    locationEditText.setTextColor(Color.GREEN);
                    locationEditText.setText(currentLocationObject.getLocationName());
                    locationEditText.setEnabled(true);
                    currentLocationButton.setClickable(true);
                    currentLocationButton.clearAnimation();
                    viewWeatherButton.setVisibility(View.VISIBLE);
                    favoriteCheck(currentLocationObject);
                    try {
                        context.unregisterReceiver(currentWeatherReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //network changes

        networkActionFilter = new IntentFilter(getString(R.string.NETWORK_ACTION));
        networkActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getString(R.string.DISCONNECTED).equals(intent.getStringExtra("TYPE"))) {
                    if (recyclerView.getAdapter() != null) {
                        Toast.makeText(context, R.string.disconnected_select_from_list, Toast.LENGTH_LONG).show();//TODO
                        viewWeatherButton.setVisibility(View.INVISIBLE);
                        currentLocationButton.setVisibility(View.INVISIBLE);
                        searchLocationsButton.setVisibility(View.INVISIBLE);
                        locationEditText.setEnabled(false);
                        isNetwork = false;
                    } else {
                        Toast.makeText(context, R.string.disconnected_and_exit, Toast.LENGTH_LONG).show();//TODO
                        unregisterReceiver(networkActionReceiver);
                        finish();

                    }
                } else {
                    if (getString(R.string.RECONNECTED).equals(intent.getStringExtra(getString(R.string.TYPE)))) {
                        Toast.makeText(context, R.string.reconnected_message, Toast.LENGTH_LONG).show();//TODO
                        currentLocationButton.setVisibility(View.VISIBLE);
                        locationEditText.setEnabled(true);
                        if (selecteditemNum != NO_ITEM_SELECTED) {
                            viewWeatherButton.setVisibility(View.VISIBLE);
                        }
                        if (recyclerView.getAdapter() != null) {
                            searchLocationsButton.setVisibility(View.VISIBLE);
                        }
                        isNetwork = true;
                    }
                }
            }
        };
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                LatLng currentLatLon = new LatLng((double) intent.getExtras().get("Latitude"), (double) intent.getExtras().get("Longitude"));
                currentLocationObject = new FavoriteLocationObject(currentLatLon);
                setCurrentWeatherReceiver();
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(locationReceiver);
            }
        };
        intentFilter = new IntentFilter(LOCATION_KEY);

        //search locations button
        choosedLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                selectedLocationObject = (FavoriteLocationObject) intent.getSerializableExtra(getString(R.string.SELECTED_LOCATION_OBJECT));

                if (selectedLocationObject != null && selectedLocationObject.getLatLng() != null) {
                    for (int i = 0; i < results.size(); i++) {
                        if (results.get(i).coord.lat == selectedLocationObject.getLatitude() && results.get(i).coord.lon == selectedLocationObject.getLongitude()) {
                            selectItemInResultsList(i);
                        }
                    }
                }

                unregisterReceiver(choosedLocationReceiver);

            }
        };
        intentFilterLocation = new IntentFilter(getString(R.string.CHOOSED_LOCATION));
    }
}