package com.example.weathertracking.sevicesAndReceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.weathertracking.screen.main.main.MainActivity.isLocationGranted;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        deliverNotification(context);
    }
    private void deliverNotification(Context context){

        try {//TODO favorites refresh!!
            if(isLocationGranted) {
                Intent refreshLocationIntent = new Intent(context, LocationService.class);
                refreshLocationIntent.putExtra("Command", "REFRESH_LOCATION");

                context.startService(refreshLocationIntent);
                Log.d("Location**", "refresh alarm");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
