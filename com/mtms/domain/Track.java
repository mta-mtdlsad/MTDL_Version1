package com.mtms.domain;

public class Track {
    public long track_id;          // Required, Primary key
    public String track_number;    // Required, Not blank
    public double latitude;        // Required, -90 to 90 (Fixed typo)
    public double longitude;       // Required, -180 to 180
    public double speed;           // Required, >0 and <=2000
    public String identity_type;   // Required, Allowed identity list
    public String track_status;    // Required, ACTIVE or INACTIVE
    public String created_time; // Required, Not null

    // Default Constructor
    public Track() {
    }

    // Parameterized Constructor
    public Track(long track_id, String track_number, double latitude, double longitude,
                 double speed, String identity_type, String track_status, String created_time) {
        this.track_id = track_id;
        this.track_number = track_number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identity_type = identity_type;
        this.track_status = track_status;
        this.created_time = created_time;
    }

    // Getters and Setters
    public long getTrack_id() {
        return track_id;
    }
    public void setTrack_id(long track_id) {
        this.track_id = track_id;
    }

    public String getTrack_number() {
        return track_number;
    }
    public void setTrack_number(String track_number) {
        this.track_number = track_number;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getIdentity_type() {
        return identity_type;
    }
    public void setIdentity_type(String identity_type) {
        this.identity_type = identity_type;
    }

    public String getTrack_status() {
        return track_status;
    }
    public void setTrack_status(String track_status) {
        this.track_status = track_status;
    }

    public String getCreated_time() {
        return created_time;
    }
    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    // To String Method
    @Override
    public String toString() {
        return "Track{" +
                "track_id: " + track_id +
                ", track_number: " + track_number +
                ", latitude: " + latitude +
                ", longitude: " + longitude +
                ", speed: " + speed +
                ", identity_type: " + identity_type +
                ", track_status: " + track_status +
                ", created_time: " + created_time +
                '}';
    }
}
