package com.mtms.domain;


import java.time.LocalDateTime;

public class Alert {
    //Attributes
    private int alertId;
    private String trackNumber;
    private String alertMessage;
    private String alertSeverity;
    private LocalDateTime alertTime;
    private String sourceId;
    private String eventType;

    // Constructor with alertId object
    public Alert(int alertId, String trackNumber, String alertMessage, String alertSeverity, LocalDateTime alertTime, String sourceId, String eventType) {
        this.alertId = alertId;
        this.trackNumber = trackNumber;
        this.alertMessage = alertMessage;
        this.alertSeverity = alertSeverity;
        this.alertTime = alertTime;
        this.sourceId = sourceId;
        this.eventType = eventType;
    }

    // Constructor without alertId object
    public Alert(String trackNumber, String alertMessage, String alertSeverity, LocalDateTime alertTime, String sourceId, String eventType) {
        this.trackNumber = trackNumber;
        this.alertMessage = alertMessage;
        this.alertSeverity = alertSeverity;
        this.alertTime = alertTime;
        this.sourceId = sourceId;
        this.eventType = eventType;
    }

    // Getters
    public int getAlertId() {
        return alertId;
    }
    public String getTrackNumber() {
        return trackNumber;
    }
    public String getAlertMessage() {
        return alertMessage;
    }
    public String getAlertSeverity() {
        return alertSeverity;
    }
    public LocalDateTime getAlertTime() {
        return alertTime;
    }
    public String getEventType() {return eventType;}
    public String getSourceId() {return sourceId;}

    @Override
    public String toString() {
        return "Alert{" +
                "alertId=" + alertId +
                ", trackNumber='" + trackNumber + '\'' +
                ", alertMessage='" + alertMessage + '\'' +
                ", alertSeverity='" + alertSeverity + '\'' +
                ", alertTime=" + alertTime +
                ", sourceId='" + sourceId + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}