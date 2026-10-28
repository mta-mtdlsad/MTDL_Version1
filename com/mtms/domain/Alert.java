package com.mtms.domain;

public class Alert {
    public long alert_id; //Required, Primary key
    public String track_number; //Not Required, May be unknown
    public String alert_message; //Required, Not blank
    public String alert_severity; //Required, LOW, MEDIUM or HIGH
    public String alert_time; //Required, Not null

    public Alert() {}

    // Parameterized Constructor
    public Alert(long alert_id, String track_number, String alert_message, String alert_severity, String alert_time) {
        this.alert_id = alert_id;
        this.track_number = track_number;
        this.alert_message = alert_message;
        this.alert_severity = alert_severity;
        this.alert_time = alert_time;
    }

    // Getters and Setters
    public long getAlert_id() {
        return alert_id;
    }
    public void setAlert_id(long alert_id) {
        this.alert_id = alert_id;
    }

    public String getTrack_number() {
        return track_number;
    }
    public void setTrack_number(String track_number) {
        this.track_number = track_number;
    }

    public String getAlert_message() {
        return alert_message;
    }
    public void setAlert_message(String alert_message) {
        this.alert_message = alert_message;
    }

    public String getAlert_severity() {
        return alert_severity;
    }
    public void setAlert_severity(String alert_severity) {
        this.alert_severity = alert_severity;
    }

    public String getAlert_time() {
        return alert_time;
    }
    public void setAlert_time(String alert_time) {
        this.alert_time = alert_time;
    }

    // To String Method
    @Override
    public String toString() {
        return "Alert{" +
                "alert_id: " + alert_id +
                ", track_number: " + track_number +
                ", alert_message: " + alert_message +
                ", alert_severity: " + alert_severity +
                ", alert_time: " + alert_time +
                '}';
    }
}
