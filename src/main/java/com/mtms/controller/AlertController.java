package com.mtms.controller;

import com.mtms.domain.Alert;
import com.mtms.domain.Track;
import com.mtms.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class AlertController {

    @Autowired
    private AlertService alertService;
    private FormController formController;

    // Get all alerts
    @GetMapping("/api/alerts/list")
    public String getAllAlerts() {

        List<Alert> listOfAlerts = alertService.getAllAlerts();

        StringBuilder result = new StringBuilder();

        if (listOfAlerts != null && !listOfAlerts.isEmpty()) {

            result.append("========== LIST ALERT RESULTS ==========<pre>");

            for (Alert alert : listOfAlerts) {
                result.append("Alert ID: ").append(alert.getAlertId()).append("\n");
                result.append("Track Number: ").append(alert.getTrackNumber()).append("\n");
                result.append("Source Id: ").append(alert.getSourceId()).append("\n");
                result.append("Alert Message: ").append(alert.getAlertMessage()).append("\n");
                result.append("Alert Severity: ").append(alert.getAlertSeverity()).append("\n");
                result.append("Event Type: ").append(alert.getEventType()).append("\n");
                result.append("------------------------------<pre>");
            }

        } else {

            result.append("Alerts could not be found.");
        }

        return result.toString();
    }

}