package com.service;

import com.domain.Alert;
import com.domain.Track;
import com.domain.ValidationResult;
import com.repository.AlertRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AlertService {
    //creating object of the alert repository
    AlertRepository alert_repo = new AlertRepository();

    //using the repo creating a new alert
    public void createAlert(Track track, ValidationResult result) {
        Alert alert = new Alert(track.getTrack_number(), result.getMessage(), result.getSeverity(), LocalDateTime.now());
        alert_repo.saveAlert(alert); // returns confirmation string
    }

    //using the repo displaying all alerts
    public List<Alert> getAlerts(){
        return alert_repo.getAllAlerts();
    }

    //clearing all alerts using track repo
    public void clearAlerts() {
        alert_repo.clearAlertsTable(); //returns confirmation string
    }
}
