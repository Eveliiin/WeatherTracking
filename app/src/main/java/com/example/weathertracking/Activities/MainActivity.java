package com.example.weathertracking.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;

import com.example.weathertracking.Adapters.PagerAdapter;
import com.example.weathertracking.Fragments.LocationDetailFragment;
import com.example.weathertracking.Network.ConnectionStateMonitor;
import com.example.weathertracking.Services.AlarmReceiver;
import com.example.weathertracking.Services.LocationService;
import com.example.weathertracking.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.weathertracking.Interfaces.InternetStateListener.isConecctedToInternet;
import static com.example.weathertracking.Services.LocationService.MY_PERMISSIONS_REQUEST_LOCATION;


public class MainActivity extends AppCompatActivity  {

    public static final String LOCATION_KEY = "location";
    @BindView(R.id.search_fab)
    protected FloatingActionButton locatonSearchFab;

    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;

    @BindView(R.id.pager)
    protected  ViewPager viewPager;

    BroadcastReceiver networkActionReceiver;
    IntentFilter networkActionFilter;
    boolean isLost=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLocationPermission();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

        ConnectionStateMonitor connectionStateMonitor = new ConnectionStateMonitor(this);
        connectionStateMonitor.enable(this);
        // TODO ? ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        if(!isConecctedToInternet()){
            Toast.makeText(this,"No internet",Toast.LENGTH_LONG).show();
            locatonSearchFab.setClickable(false);
        }
        else {
            startLocationService();
        }
        setRefresh();
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

        long repeatInterval = AlarmManager.INTERVAL_DAY;
        long triggerTime = SystemClock.elapsedRealtime();
        //+ repeatInterval;

        if(alarmManager!=null) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
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
    }
    void initNetworkActionReceiver(){
        //Network action refreshReceiver
        networkActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if("DISCONNECTED".equals(intent.getStringExtra("TYPE"))){

                    locatonSearchFab.setClickable(true);
                    locatonSearchFab.setBackgroundColor(Color.LTGRAY);
                    if(isLost) {
                        Toast.makeText(getApplicationContext(), "reconnected", Toast.LENGTH_LONG).show();
                        isLost=false;
                    }
                }
                else {
                    if("RECONNECTED".equals(intent.getStringExtra("TYPE"))){
                        isLost=true;
                        locatonSearchFab.setClickable(false);
                        locatonSearchFab.setBackgroundColor(Color.LTGRAY);
                    }
                }
            }
        };
        networkActionFilter = new IntentFilter("NETWORK_ACTION");
    }

    public class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context context , Intent intent) {
            String action = intent.getAction() ;
            if (LOCATION_KEY.equals(action)) {

                //TODO ???
                Intent closeIntent = new Intent(context, LocationService.class);
                closeIntent.putExtra("Command","STOP");
                context.startService(closeIntent);
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("message","Notification closed");
                context.startActivity(i);
            }
        }
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location permission request")
                        .setMessage("Please enable location.")
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
        //TODO ne folytassa ha false vagy valami
    }
}