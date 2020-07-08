package com.example.weathertracking.sevicesAndReceiver;

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.weathertracking.screen.main.MainActivity;

public class LocationService extends IntentService
{
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private static final int TEN_MINUTES = 1000 * 60 * 1;
    private static final int  THREE_KM = 3000;
    public LocationManager locationManager=null;
    public MyLocationListener listener=null;
    public Location previousBestLocation = null;
    Intent intent;
    Boolean isRefreshRequested= false;

    public LocationService() {
        super("name");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {


        String command;
        if (intent != null) {
            command=intent.getStringExtra("Command");
            if(command!=null){
                switch (command){
                    case "START": registerListeners();
                        break;

                    case "STOP": locationManager.removeUpdates(listener);
                        break;
                    case "REFRESH_LOCATION":
                        Log.d("Location**","refrescommand");
                        isRefreshRequested=true;
                        registerListeners();
                        break;
                }
            }
        }
        return START_NOT_STICKY;
    }


    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {

            Log.i("LOCATION", "Location changed: "+loc.getLatitude()+","+loc.getLongitude());
            if(isBetterLocation(loc, previousBestLocation)||isRefreshRequested) {


                intent = new Intent(MainActivity.LOCATION_KEY);

                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());

                intent.setAction(MainActivity.LOCATION_KEY);

                //sendBroadcast(intent);
                LocalBroadcastManager
                        .getInstance(getApplicationContext())
                        .sendBroadcast(intent.putExtra("broadcastMessage", loc.getLatitude()+", "+loc.getLongitude()));

                Log.d("Location**","location sended");
                SystemClock.sleep(1000);
                previousBestLocation=loc;

                if(isRefreshRequested){
                    isRefreshRequested=false;
                }
                locationManager.removeUpdates(listener);

            }
        }


        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }
        public void onProviderEnabled(String provider) {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            //currentBestLocation=location;

            return true;
        }
        if( location.distanceTo(currentBestLocation)>THREE_KM){
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeElapsed = location.getTime() - currentBestLocation.getTime();

        return timeElapsed > TEN_MINUTES;// nem jo, mert le lesz iratkozva IDE KELL AZ EBRESZTES

    }
    private void registerListeners(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
        }
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

    }
    private void sssss(){
        /*if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
        }else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        }*/    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
        }
        if(locationManager!=null){
            locationManager.removeUpdates(listener);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) { }

}