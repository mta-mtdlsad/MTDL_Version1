package com.domain;

import java.time.LocalDateTime;

public class Track {
    int track_id;
    String track_number;
    double latitude;
    double longitude;
    double speed;
    String identity_type;
    String track_status;
    LocalDateTime created_time;

    public Track(int track_id, String track_number, double latitude, double longitude, double speed, String identity_type, String track_status, LocalDateTime created_time) {
        this.track_id = track_id;
        this.track_number = track_number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identity_type = identity_type;
        this.track_status = track_status;
        this.created_time = created_time;
    }

    public int getTrack_id() {
        return track_id;
    }

    public String getTrack_number() {
        return track_number;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public String getIdentity_type() {
        return identity_type;
    }

    public String getTrack_status() {
        return track_status;
    }

    public LocalDateTime getCreated_time() {
        return created_time;
    }

    @Override
    public String toString() {
        return "Track{" +
                "track_id=" + track_id +
                ", track_number='" + track_number + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", identity_type='" + identity_type + '\'' +
                ", track_status='" + track_status + '\'' +
                ", created_time=" + created_time +
                '}';
    }
}
