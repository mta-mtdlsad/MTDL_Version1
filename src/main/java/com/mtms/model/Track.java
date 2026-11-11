package com.mtms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Track{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Track(){

    }

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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return trackId == track.trackId && sequenceNumber == track.sequenceNumber && Double.compare(latitude, track.latitude) == 0 && Double.compare(longitude, track.longitude) == 0 && Double.compare(speed, track.speed) == 0 && Objects.equals(sourceId, track.sourceId) && Objects.equals(trackNumber, track.trackNumber) && Objects.equals(identityType, track.identityType) && Objects.equals(trackStatus, track.trackStatus) && Objects.equals(createdTime, track.createdTime) && Objects.equals(recievedTime, track.recievedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, sourceId, sequenceNumber, trackNumber, latitude, longitude, speed, identityType, trackStatus, createdTime, recievedTime);
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
