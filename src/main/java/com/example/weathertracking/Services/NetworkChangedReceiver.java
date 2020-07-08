package com.example.weathertracking.sevicesAndReceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}
