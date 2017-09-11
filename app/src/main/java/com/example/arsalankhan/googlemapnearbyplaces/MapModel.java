package com.example.arsalankhan.googlemapnearbyplaces;

/**
 * Created by Arsalan khan on 9/11/2017.
 */

public class MapModel {

    String name;
    String vicinity;
    double longitude;
    double latitude;

    public MapModel(String name, String vicinity, double longitude, double latitude) {
        this.name = name;
        this.vicinity = vicinity;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
