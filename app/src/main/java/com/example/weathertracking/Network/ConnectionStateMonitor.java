package com.example.weathertracking.Network;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static com.example.weathertracking.Interfaces.InternetStateListener.isConecctedToInternet;

public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    final NetworkRequest networkRequest;

    Context context;
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

        Intent noInternetIntent = new Intent("NETWORK_ACTION");
        noInternetIntent.putExtra("TYPE","RECONNECTED");
        context.sendBroadcast(noInternetIntent);
    }

    @Override//ez
    public void onLost(@NonNull Network network) {
        if(!isConecctedToInternet()) {
            super.onLost(network);
            Toast.makeText(context, "no internet", Toast.LENGTH_LONG).show();

            Intent noInternetIntent = new Intent("NETWORK_ACTION");
            noInternetIntent.putExtra("TYPE", "DISCONNECTED");
            context.sendBroadcast(noInternetIntent);
        }
    }
}