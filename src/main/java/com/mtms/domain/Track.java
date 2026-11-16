package com.mtms.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
//@Table(name = "track")
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

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }
    public void setReceivedTime(LocalDateTime receivedTime) {
        this.receivedTime = receivedTime;
    }
    public void setObservationTime(LocalDateTime observationTime) {
        this.observationTime = observationTime;
    }
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public void setTrackStatus(String trackStatus) {
        this.trackStatus = trackStatus;
    }
    public void setIdentity(String identity) {
        this.identity = identity;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Double.compare(latitude, track.latitude) == 0 && Double.compare(longitude, track.longitude) == 0 && Double.compare(speed, track.speed) == 0 && sequenceNumber == track.sequenceNumber && Objects.equals(trackId, track.trackId) && Objects.equals(trackNumber, track.trackNumber) && Objects.equals(identity, track.identity) && Objects.equals(trackStatus, track.trackStatus) && Objects.equals(sourceId, track.sourceId) && Objects.equals(observationTime, track.observationTime) && Objects.equals(receivedTime, track.receivedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, trackNumber, latitude, longitude, speed, identity, trackStatus, sourceId, sequenceNumber, observationTime, receivedTime);
    }
}