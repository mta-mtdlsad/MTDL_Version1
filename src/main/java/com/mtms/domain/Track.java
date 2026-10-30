package com.mtms.domain;

import java.time.LocalDateTime;

public class Track {
    private int trackId;
    private String trackNumber;
    private double latitude;
    private double longitude;
    private float speed;
    private String identity;
    private String trackStatus;
    private LocalDateTime createdTime;

    // Constructor with trackId object
    public Track(int trackId, String trackNumber, double latitude, double longitude,
                 float speed, String identity, String trackStatus, LocalDateTime createdTime) {
        this.trackId = trackId;
        this.trackNumber = trackNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identity = identity;
        this.trackStatus = trackStatus;
        this.createdTime = createdTime;
    }

    // Constructor without trackId object
    public Track(String trackNumber, double latitude, double longitude,
                 float speed, String identity, String trackStatus, LocalDateTime createdTime) {
        this.trackNumber = trackNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identity = identity;
        this.trackStatus = trackStatus;
        this.createdTime = createdTime;
    }

    // Getters
    public int getTrackId() {
        return trackId;
    }
    public String getTrackNumber() {
        return trackNumber;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public float getSpeed() {
        return speed;
    }
    public String getIdentity() {
        return identity;
    }
    public String getTrackStatus() {
        return trackStatus;
    }
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    // toString
    @Override
    public String toString() {
        return "Track{" +
                "trackId='" + trackId + '\'' +
                ", trackNumber='" + trackNumber + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", identity='" + identity + '\'' +
                ", trackStatus='" + trackStatus + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}