package com.example.weathertracking.network;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    private final NetworkRequest networkRequest;
    private Context context;


    public ConnectionStateMonitor(Context context) {
        this.context=context;
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void enable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(networkRequest, this);
        }
    }


    // Likewise, you can have a disable method that simply calls ConnectivityManager.unregisterNetworkCallback(NetworkCallback) too.

    @Override//ez
    public void onAvailable(Network network) {
        // Do what you need to do here

        Intent availableIntent = new Intent("NETWORK_ACTION");
        availableIntent.putExtra("TYPE","RECONNECTED");
        LocalBroadcastManager.getInstance(context).sendBroadcast(availableIntent);
    }

    @Override//ez
    public void onLost(@NonNull Network network) {
        if(!isConnectedToInternet()) {
            super.onLost(network);
            Toast.makeText(context, "no internet", Toast.LENGTH_LONG).show();

            Intent noInternetIntent = new Intent("NETWORK_ACTION");
            noInternetIntent.putExtra("TYPE", "DISCONNECTED");
            LocalBroadcastManager.getInstance(context).sendBroadcast(noInternetIntent);
        }
    }

    public static boolean isConnectedToInternet() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e)          { e.printStackTrace(); }
        return false;
    }
}