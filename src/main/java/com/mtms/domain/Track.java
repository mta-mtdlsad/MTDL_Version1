package com.mtms.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "track")
public class Track {
    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer trackId;
    private String trackNumber;
    private double latitude;
    private double longitude;
    private double speed;
    private String identity;
    private String trackStatus;
    private String sourceId;
    private int sequenceNumber;
    private LocalDateTime observationTime;
    private LocalDateTime receivedTime;

    // Constructor with trackId object
    public Track(Integer trackId, String trackNumber, double latitude, double longitude,
                 double speed, String identity, String trackStatus, String sourceId, int sequenceNumber,
                 LocalDateTime observationTime,  LocalDateTime receivedTime) {
        this.trackId = trackId;
        this.trackNumber = trackNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identity = identity;
        this.trackStatus = trackStatus;
        this.sourceId = sourceId;
        this.sequenceNumber = sequenceNumber;
        this.observationTime = observationTime;
        this.receivedTime = receivedTime;
    }

    // Constructor without trackId object
    public Track(String trackNumber, double latitude, double longitude,
                 double speed, String identity, String trackStatus, String sourceId, int sequenceNumber,
                 LocalDateTime observationTime,  LocalDateTime receivedTime) {
        this.trackNumber = trackNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.identity = identity;
        this.trackStatus = trackStatus;
        this.sourceId = sourceId;
        this.sequenceNumber = sequenceNumber;
        this.observationTime = observationTime;
        this.receivedTime = receivedTime;
    }

    public Track() {}

    // Getters
    public Integer getTrackId() {
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
    public double getSpeed() {
        return speed;
    }
    public String getIdentity() {
        return identity;
    }
    public String getTrackStatus() {
        return trackStatus;
    }
    public String getSourceId() {return sourceId;}
    public int getSequenceNumber() {return sequenceNumber;}
    public LocalDateTime getObservationTime() {return observationTime;}
    public LocalDateTime getReceivedTime() {return receivedTime;}

    @Override
    public String toString() {
        return "Track{" +
                "trackId=" + trackId +
                ", trackNumber='" + trackNumber + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", identity='" + identity + '\'' +
                ", trackStatus='" + trackStatus + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", observationTime=" + observationTime +
                ", receivedTime=" + receivedTime +
                '}';
    }
}