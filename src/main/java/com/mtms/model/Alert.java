package com.mtms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int alertId;

    String trackNumber;
    String alertMessage;
    String alertSeverity;
    LocalDateTime alertTime;

    // for JPA
    protected Alert() {
    }

    public Alert(int alertId, String trackNumber, String alertMessage, String alertSeverity, LocalDateTime alertTime) {
        this.alertId = alertId;
        this.trackNumber = trackNumber;
        this.alertMessage = alertMessage;
        this.alertSeverity = alertSeverity;
        this.alertTime = alertTime;
    }

    public Alert(String trackNumber, String alertMessage, String alertSeverity, LocalDateTime alertTime) {
        this.trackNumber = trackNumber;
        this.alertMessage = alertMessage;
        this.alertSeverity = alertSeverity;
        this.alertTime = alertTime;
    }

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

    @Override
    public String toString() {
        return "Alert{" +
                "alertId=" + alertId +
                ", trackNumber='" + trackNumber + '\'' +
                ", alertMessage='" + alertMessage + '\'' +
                ", alertSeverity='" + alertSeverity + '\'' +
                ", alertTime=" + alertTime +
                '}';
    }
}
