package com.mtms.model;

import java.util.Map;

public class FilterParameters {
    private String sourceId;
    private String trackNumber;
    private String identity;
    private String status;
    private String minSpeed;
    private String maxSpeed;
    private String observationType;
    private String startTime;
    private String endTime;
    private String timeSort;
    private String timeSortDirection;
    private String speedSort;
    private String speedSortDirection;
    private String trackNumberSort;
    private String trackNumberSortDirection;

    FilterParameters() {}

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(String minSpeed) {
        this.minSpeed = minSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getObservationType() {
        return observationType;
    }

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTimeSort() {
        return timeSort;
    }

    public void setTimeSort(String timeSort) {
        this.timeSort = timeSort;
    }

    public String getTimeSortDirection() {
        return timeSortDirection;
    }

    public void setTimeSortDirection(String timeSortDirection) {
        this.timeSortDirection = timeSortDirection;
    }

    public String getSpeedSort() {
        return speedSort;
    }

    public void setSpeedSort(String speedSort) {
        this.speedSort = speedSort;
    }

    public String getSpeedSortDirection() {
        return speedSortDirection;
    }

    public void setSpeedSortDirection(String speedSortDirection) {
        this.speedSortDirection = speedSortDirection;
    }

    public String getTrackNumberSort() {
        return trackNumberSort;
    }

    public void setTrackNumberSort(String trackNumberSort) {
        this.trackNumberSort = trackNumberSort;
    }

    public String getTrackNumberSortDirection() {
        return trackNumberSortDirection;
    }

    public void setTrackNumberSortDirection(String trackNumberSortDirection) {
        this.trackNumberSortDirection = trackNumberSortDirection;
    }

    @Override
    public String toString() {
        return "FilterParameters{" +
                "sourceId='" + sourceId + '\'' +
                ", trackNumber='" + trackNumber + '\'' +
                ", identity='" + identity + '\'' +
                ", status='" + status + '\'' +
                ", minSpeed='" + minSpeed + '\'' +
                ", maxSpeed='" + maxSpeed + '\'' +
                ", observationType='" + observationType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", timeSort='" + timeSort + '\'' +
                ", timeSortDirection='" + timeSortDirection + '\'' +
                ", speedSort='" + speedSort + '\'' +
                ", speedSortDirection='" + speedSortDirection + '\'' +
                ", trackNumberSort='" + trackNumberSort + '\'' +
                ", trackNumberSortDirection='" + trackNumberSortDirection + '\'' +
                '}';
    }
}
