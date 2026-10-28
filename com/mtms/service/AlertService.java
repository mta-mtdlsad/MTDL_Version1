package com.mtms.service;

import com.mtms.constants.MTMSConstants;
import com.mtms.domain.Alert;
import com.mtms.repository.AlertRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertService {
    private final AlertRepository alertRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }


    public void createAlert(String trackNumber, String message) {
        String severity = chooseSeverity(message);
        String timestamp = LocalDateTime.now().format(formatter);

        // Map data directly to  Alert table structure
        Alert alert = new Alert(0, trackNumber, message, severity, timestamp);

        // Save using repository
        alertRepository.insert(alert);

        // Print saved alert
        System.out.println("Saved Alert: [Track: " + (trackNumber != null ? trackNumber : "UNKNOWN") +
                " | Msg: " + message + " | Severity: " + severity + "]");
    }

    private String chooseSeverity(String message) {
        if (message == null) return MTMSConstants.alertLOW;

        String msgLower = message.toLowerCase();
        if (msgLower.contains("latitude") || msgLower.contains("longitude")) {
            return MTMSConstants.alertHigh; // if lat is not accepted
        } else if (msgLower.contains("speed") || msgLower.contains("allowed-limit")) {
            return MTMSConstants.alertMid; // if speed is not accepted
        }
        return MTMSConstants.alertLOW; // if identity and tracking status not accepted
    }
}
