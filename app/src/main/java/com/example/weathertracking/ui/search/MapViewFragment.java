package com.example.weathertracking.ui.search;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weathertracking.models.MapMarker;
import com.example.weathertracking.R;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
/*
import com.threess.summership.treasurehunt.fragment.home_menu.HideTreasureFragment;
import com.threess.summership.treasurehunt.logic.ApiController;
import com.threess.summership.treasurehunt.navigation.FragmentNavigation;
import com.threess.summership.treasurehunt.util.Util;*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    public static final String TAG = MapViewFragment.class.getSimpleName();
    public static final String KEY1="key1";
    public static final String KEY2="key2";

    @BindView(R.id.mapView)
    protected MapView mMapView;


    private ArrayList<MapMarker> mapMarkers = new ArrayList<>();
    private GoogleMap googleMap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getParentFragmentManager()
        //        .findFragmentById(R.id.mapView);
        //mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        getTreasures();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawMap() {
        mMapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        changeFocus(googleMap);
        drawMarkers(googleMap);
        getCurrentLocation(googleMap);
        googleMap.setOnMapLongClickListener(this);
    }

    private void drawMarkers(GoogleMap googleMap) {
        for (MapMarker it : mapMarkers) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(it.getLocationLat(),
                            it.getLocationLon())).title(it.getDescription()));
            //TODO
        }
    }


    private void changeFocus(GoogleMap googleMap) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(46.544595, 24.561126));
        googleMap.moveCamera(changeFocus(marker));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
    }

    private CameraUpdate changeFocus(MarkerOptions position) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder().include(position.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);
        return CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
    }

    private void getCurrentLocation(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {

        }
        googleMap.setMyLocationEnabled(true);
    }


    private void getTreasures(){
        mapMarkers.clear();

        MapMarker debrecen = new MapMarker(47.5569536,21.6271677);
        MapMarker vasarhely = new MapMarker(46.5429517,24.5604468);
        mapMarkers.add(debrecen);
        mapMarkers.add(vasarhely);
        drawMap();
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        /*googleMap.addMarker(new MarkerOptions().position(latLng).
                title("").icon(BitmapDescriptorFactory.
                fromBitmap(Util.getDrawableTreasureImage(getContext()))));*/

        //TODO FragmentNavigation.getInstance(getContext()).showHomeFragmentWithPicker(latLng.latitude, latLng.longitude);
    }
    /*
    public static HideTreasureFragment newInstance(double latitude , double longitude){
        HideTreasureFragment hideTreasureFragment=new HideTreasureFragment();
        Bundle args=new Bundle();
        args.putDouble(KEY1,latitude);
        args.putDouble(KEY2,longitude);
        hideTreasureFragment.setArguments(args);

        return hideTreasureFragment;
    }*/

}
