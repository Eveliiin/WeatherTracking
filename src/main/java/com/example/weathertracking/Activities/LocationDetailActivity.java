package com.example.weathertracking.main;


import android.os.Bundle;
import android.util.Log;


import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.Fragments.LocationDetailFragment;
import com.example.weathertracking.R;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.weathertracking.Fragments.LocationDetailFragment.FAVORITE_LOCATION_OBJECT;

public class LocationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            LocationDetailFragment locationDetailFragment;
            FavoriteLocationObject favoriteLocationObject= (FavoriteLocationObject) extras.getSerializable(FAVORITE_LOCATION_OBJECT);
            if(favoriteLocationObject!=null){
            locationDetailFragment = new LocationDetailFragment(favoriteLocationObject);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.locationDetailFragment, locationDetailFragment).commit();

            }
            else {
                Log.e("LOCATION DETAIL ACTIVITY","null object");
            }
        }
    }
}