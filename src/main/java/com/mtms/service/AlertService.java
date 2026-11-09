package com.mtms.service;

import com.mtms.model.Alert;
import com.mtms.model.Track;
import com.mtms.model.ValidationResult;
import com.mtms.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {
    //creating object of the alert repository
    @Autowired
    private AlertRepository alert_repo;

    //using the repo creating a new alert
    public void createAlert(Track track, ValidationResult result) {
        String trackNumber = null;
        // Malformed UDP messages may not have a Track object.
        if (track != null) {
            trackNumber = track.getTrackNumber();
        }
        Alert alert = new Alert(
                trackNumber,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        alert_repo.saveAlert(alert);
    }

    //using the repo creating a new alert
    public void createAlert(String trackNo, ValidationResult result) {
        Alert alert = new Alert(
                trackNo,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        alert_repo.saveAlert(alert);
    }

    //using the repo creating a new alert
    public void createAlert(ValidationResult result) {
        Alert alert = new Alert(
                null,
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
