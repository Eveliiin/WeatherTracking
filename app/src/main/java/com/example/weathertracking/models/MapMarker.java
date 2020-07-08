package com.example.weathertracking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MapMarker implements Serializable {
    private static final String TAG = MapMarker.class.getSimpleName();


    //TODO LatLon
    @SerializedName("description")
    @Expose
    private String mDescription;


    @SerializedName("location_lat")
    @Expose
    private double mLocationLat;

    @SerializedName("location_lon")
    @Expose
    private double mLocationLon;

    private String mUpdatedAt;

    public MapMarker(double lat, double lon){
        this.mLocationLat=lat;
        this.mLocationLon=lon;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public double getLocationLat() {
        return mLocationLat;
    }

    public void setLocationLat(double location_lat) {
        this.mLocationLat = location_lat;
    }

    public double getLocationLon() {
        return mLocationLon;
    }

    public void setLocationLon(double location_lon) {
        this.mLocationLon = location_lon;
    }
    public String getUpdatedAt() {
        return mUpdatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.mUpdatedAt = updatedAt;
    }

}