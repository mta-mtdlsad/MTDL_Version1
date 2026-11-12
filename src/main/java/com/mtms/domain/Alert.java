package com.mtms.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
//@Table(name = "alert")
public class Alert {
    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertId;
    private String trackNumber;
    private String alertMessage;
    private String alertSeverity;
    private LocalDateTime alertTime;
    private String sourceId;
    private String eventType;

    // default constructor
    public Alert() {}

    // Constructor with alertId object
    public Alert(Integer alertId, String trackNumber, String alertMessage, String alertSeverity, LocalDateTime alertTime, String sourceId, String eventType) {
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
    public Integer getAlertId() {
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