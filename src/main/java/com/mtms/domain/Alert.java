package com.mtms.domain;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Alert {
    private int alertId;
    private String trackNumber;
    private String alertMessage;
    private String alertSeverity;
    private LocalDateTime alertTime;

    // Constructor with alertId object
    public Alert(int alertId, String trackNumber, String alertMessage, String alertSeverity, LocalDateTime alertTime) {
        this.alertId = alertId;
        this.trackNumber = trackNumber;
        this.alertMessage = alertMessage;
        this.alertSeverity = alertSeverity;
        this.alertTime = alertTime;
    }

    // Constructor without alertId object
    public Alert(String trackNumber, String alertMessage, String alertSeverity, LocalDateTime alertTime) {
        this.trackNumber = trackNumber;
        this.alertMessage = alertMessage;
        this.alertSeverity = alertSeverity;
        this.alertTime = alertTime;
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

    // toString()
    @Override
    public String toString() {
        return "Alert{" +
                "alertId='" + alertId + '\'' +
                ", trackNumber='" + trackNumber + '\'' +
                ", alertMessage='" + alertMessage + '\'' +
                ", alertSeverity='" + alertSeverity + '\'' +
                ", alertTime=" + alertTime +
                '}';
    }
}