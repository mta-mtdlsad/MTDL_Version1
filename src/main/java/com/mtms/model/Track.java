package com.mtms.model;

import java.time.LocalDateTime;

public class Track{
    int trackId;
    String sourceId;
    int sequenceNumber;
    String trackNumber;
    double latitude;
    double longitude;
    double speed;
    String identityType;
    String trackStatus;
    LocalDateTime createdTime;
    LocalDateTime recievedTime;

    public Track(int trackId, String sourceId, int sequenceNumber, String trackNumber, double latitude, double longitude, double speed, String identityType, String trackStatus, LocalDateTime createdTime) {
        this.trackId = trackId;
        this.sourceId = sourceId;
        this.sequenceNumber = sequenceNumber;
        this.trackNumber = trackNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identityType = identityType;
        this.trackStatus = trackStatus;
        this.createdTime = createdTime;
    }

    public Track(int trackId, String sourceId, int sequenceNumber, String trackNumber, double latitude, double longitude, double speed, String identityType, String trackStatus, LocalDateTime createdTime, LocalDateTime recievedTime) {
        this.trackId = trackId;
        this.sourceId = sourceId;
        this.sequenceNumber = sequenceNumber;
        this.trackNumber = trackNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identityType = identityType;
        this.trackStatus = trackStatus;
        this.createdTime = createdTime;
        this.recievedTime = recievedTime;
    }

    public Track(String sourceId, int sequenceNumber, String trackNumber, double latitude, double longitude, double speed, String identityType, String trackStatus, LocalDateTime createdTime) {
        this.sourceId = sourceId;
        this.sequenceNumber = sequenceNumber;
        this.trackNumber = trackNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identityType = identityType;
        this.trackStatus = trackStatus;
        this.createdTime = createdTime;
        this.recievedTime = recievedTime;
    }

    public int getTrackId() {
        return trackId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
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

    public String getIdentityType() {
        return identityType;
    }

    public String getTrackStatus() {
        return trackStatus;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getRecievedTime() {
        return recievedTime;
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackId=" + trackId +
                ", sourceId='" + sourceId + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", trackNumber='" + trackNumber + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", identityType='" + identityType + '\'' +
                ", trackStatus='" + trackStatus + '\'' +
                ", createdTime=" + createdTime +
                ", recievedTime=" + recievedTime +
                '}';
    }
}
