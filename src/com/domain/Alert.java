package com.domain;

import java.time.LocalDateTime;

public class Alert {
    int alert_id;
    String track_number;
    String alert_message;
    String alert_severity;
    LocalDateTime alert_time;

    public Alert(int alert_id, String track_number, String alert_message, String alert_severity, LocalDateTime alert_time) {
        this.alert_id = alert_id;
        this.track_number = track_number;
        this.alert_message = alert_message;
        this.alert_severity = alert_severity;
        this.alert_time = alert_time;
    }

    public int getAlert_id() {
        return alert_id;
    }

    public String getTrack_number() {
        return track_number;
    }

    public String getAlert_message() {
        return alert_message;
    }

    public String getAlert_severity() {
        return alert_severity;
    }

    public LocalDateTime getAlert_time() {
        return alert_time;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "alert_id=" + alert_id +
                ", track_number='" + track_number + '\'' +
                ", alert_message='" + alert_message + '\'' +
                ", alert_severity='" + alert_severity + '\'' +
                ", alert_time=" + alert_time +
                '}';
    }
}
