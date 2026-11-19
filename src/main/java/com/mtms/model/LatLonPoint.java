package com.mtms.model;

import java.util.Locale;

public class LatLonPoint {
    private final double lat;
    private final double lon;

    public LatLonPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() { return lat; }
    public double getLon() { return lon; }

    @Override
    public String toString() {
        return String.format(Locale.US, "{lat: %.6f, lon: %.6f}", lat, lon);
    }
}
