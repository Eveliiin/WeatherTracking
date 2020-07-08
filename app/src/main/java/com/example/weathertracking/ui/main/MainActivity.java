package com.example.weathertracking.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.example.weathertracking.adapters.PagerAdapter;
import com.example.weathertracking.network.ConnectionStateMonitor;
import com.example.weathertracking.ui.search.LocationSearchActivity;
import com.example.weathertracking.sevicesAndReceiver.AlarmReceiver;
import com.example.weathertracking.sevicesAndReceiver.LocationService;
import com.example.weathertracking.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.weathertracking.network.ConnectionStateMonitor.isConnectedToInternet;


public class MainActivity extends AppCompatActivity  {

    public static final String LOCATION_KEY = "location";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    @BindView(R.id.search_fab)
    protected FloatingActionButton locatonSearchFab;

    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;

    @BindView(R.id.pager)
    protected  ViewPager viewPager;

    BroadcastReceiver networkActionReceiver;
    IntentFilter networkActionFilter;
    public static boolean isLocationGranted =false;
    boolean isConnectionLost =false;

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationGranted=true;
        }
        else {isLocationGranted=false;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        isLocationGranted=checkLocationPermission2();
        ConnectionStateMonitor connectionStateMonitor = new ConnectionStateMonitor(this);
        connectionStateMonitor.enable(this);
        // TODO ? ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        init();
        if(!isConnectedToInternet()){
            Toast.makeText(this,"No internet",Toast.LENGTH_LONG).show();
            locatonSearchFab.setClickable(false);
        }
        else {
            if(isLocationGranted){
            startLocationService();
            }
        }
        setRefresh();
        BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ( action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) ) {
                    ComponentName thiswidget = new ComponentName(context,     MainActivity.class);
                    AppWidgetManager am = AppWidgetManager.getInstance(context);
                    int[] ids = am.getAppWidgetIds(thiswidget);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

                    // either handle the permission change at this point or in onUpdate()
                }
            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
    }

    public void startLocationService(){
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        intent.putExtra(getString(R.string.COMMAND),"START");
        startService(intent);
    }
    private void setRefresh(){
        //Notification manager
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        long repeatInterval = AlarmManager.INTERVAL_HALF_DAY;
        long triggerTime = SystemClock.elapsedRealtime();
        //+ repeatInterval;

        if(alarmManager!=null) {
            //inexact
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime+repeatInterval, repeatInterval, notifyPendingIntent);
        }
    }

    // TODO Typeface tf =Typeface.createFromAsset( getAssets("weather.ttf"));
    private void init(){
        locatonSearchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationSearchActivity.class);
                startActivity(intent);
            }
        });
        // Set the text for each tab.

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager);
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount()) {
        };
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==1){
                    Intent animationIntent = new Intent(getString(R.string.START_ANIMATION));
                    sendBroadcast(animationIntent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.getTabAt(0).setText("Locations");
        tabLayout.getTabAt(1).setText("Current Location");
        initNetworkActionReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(networkActionReceiver,networkActionFilter);
    }
    void initNetworkActionReceiver(){
        //Network action refreshReceiver
        networkActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if("DISCONNECTED".equals(intent.getStringExtra("TYPE"))){

                    locatonSearchFab.setClickable(true);
                    locatonSearchFab.setBackgroundColor(Color.LTGRAY);
                }
                else {
                    if("RECONNECTED".equals(intent.getStringExtra("TYPE"))){
                        isConnectionLost =true;
                        locatonSearchFab.setClickable(false);
                        locatonSearchFab.setBackgroundColor(Color.LTGRAY);
                    }
                }
            }
        };
        networkActionFilter = new IntentFilter("NETWORK_ACTION");
    }

    public  boolean checkLocationPermission2() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("please enable location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationGranted = true;
                // permission was granted, yay! Do the
                // location-related task you need to do.

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    recreate();
                    startLocationService();
                    //Request location updates:
                    //locationManager.requestLocationUpdates(provider, 400, 1, this);
                }
            } else {
                isLocationGranted = false;
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }
}