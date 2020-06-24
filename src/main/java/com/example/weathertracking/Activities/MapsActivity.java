package com.example.weathertracking.screen.main;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.weathertracking.models.FavoriteLocationObject;
import com.example.weathertracking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    List<FavoriteLocationObject> listOfResults;
    Button mChooseButton;
    int selectionCount;
    LatLng selectedMarkerCord=null;
    String selectedLocation ="";
    int selectedListItemNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listOfResults= (List<FavoriteLocationObject>) getIntent().getExtras().getSerializable("list");
        selectedListItemNum = getIntent().getIntExtra("CHOOSED",-2);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mChooseButton= findViewById(R.id.chooseButton);
        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendChoosedLocation = new Intent("CHOOSED_LOCATION");
                sendChoosedLocation.putExtra("COORDINATES",selectedMarkerCord);
                sendChoosedLocation.putExtra("NAME",selectedLocation);
                v.getContext().sendBroadcast(sendChoosedLocation);
                finish();

            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        for(FavoriteLocationObject location: listOfResults){

            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatLng().latitude,location.getLatLng().longitude)).title(location.getLocationName()));

        }
        if(selectedListItemNum>-1){
            FavoriteLocationObject selectedInList= listOfResults.get(selectedListItemNum);
            float zoomLevel = 12.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedInList.getLatLng(),zoomLevel));

            selectedMarkerCord=selectedInList.getLatLng();
            selectedLocation= selectedInList.getLocationName();

        }
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        selectionCount=0;
    }


    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();
        selectedMarkerCord=marker.getPosition();
        selectedLocation= marker.getTitle();

        // Check if a click count was set, then display the click count.
        if(selectionCount==0) {
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() + " has been selected.",
                    Toast.LENGTH_SHORT).show();

            float zoomLevel = 12.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomLevel));
            mChooseButton.setVisibility(View.VISIBLE);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

}
