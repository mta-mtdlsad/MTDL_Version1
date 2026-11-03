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
        String trackNumber = null;
        // Malformed UDP messages may not have a Track object.
        if (track != null) {
            trackNumber = track.getTrack_number();
        }
        Alert alert = new Alert(
                trackNumber,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        alert_repo.saveAlert(alert);
    }
    //using the repo displaying all alerts
    public List<Alert> getAlerts(){
        List<Alert> alerts = alert_repo.getAllAlerts();
        if (alerts.isEmpty()){
            System.out.println("No Alerts found in the database");
        }
        return alerts;
    }

    //clearing all alerts using track repo
    public void clearAlerts() {
        Boolean alert = alert_repo.clearAlertsTable();
        if (alert == false){
            System.out.println("Alert table not cleared from the database");
            return;
        }
        alert_repo.clearAlertsTable();
        System.out.println("Alert table cleared successfully"); //prints confirmation string
    }
}
