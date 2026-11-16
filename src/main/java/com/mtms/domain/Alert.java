package com.mtms.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    private String trackNumber;
    private String sourceId;
    private String alertMessage;
    private String alertSeverity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alertTime;

    private String eventType;

    public Alert() {}

    public Alert(String trackNumber, String sourceId, String alertMessage, String alertSeverity, LocalDateTime alertTime, String eventType) {
        this.trackNumber = trackNumber;
        this.sourceId = sourceId;
        this.alertMessage = alertMessage;
        this.alertSeverity = alertSeverity;
        this.alertTime = alertTime;
        this.eventType = eventType;
    }

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public String getTrackNumber() { return trackNumber; }
    public void setTrackNumber(String trackNumber) { this.trackNumber = trackNumber; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }

    public String getAlertSeverity() { return alertSeverity; }
    public void setAlertSeverity(String alertSeverity) { this.alertSeverity = alertSeverity; }

    public LocalDateTime getAlertTime() { return alertTime; }
    public void setAlertTime(LocalDateTime alertTime) { this.alertTime = alertTime; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    @Override
    public String toString() {
        return "Alert{" +
                "alertId=" + alertId +
                ", trackNumber='" + trackNumber + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", alertMessage='" + alertMessage + '\'' +
                ", alertSeverity='" + alertSeverity + '\'' +
                ", alertTime=" + alertTime +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
