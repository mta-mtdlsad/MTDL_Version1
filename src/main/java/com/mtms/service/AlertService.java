package com.mtms.service;

import com.mtms.domain.Alert;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;


    // Process alert and save in repository
    public String processAlert(String sourceId, String trackNumber, String alertMessage, String eventType) {

        // processes short track number
        if (alertMessage.contains("short")) {
            trackNumber = "INVALID(" + trackNumber + ")";
        }

        // create alert object and save to repository, assigning severity base on event type
        return createAlertObject(trackNumber, alertMessage, chooseSeverity(eventType), sourceId, eventType);
    }

    // Choose severity based on track event
    private static String chooseSeverity(String eventType) {
        return switch (eventType) {
            case "Malformed message", "Source becomes disconnected"  -> "HIGH";
            case "Track validation failure", "Source becomes stale" -> "MEDIUM";
            case "Duplicate message", "Out-of-order message" -> "LOW";
            default -> null;
        };
    }

    // Create the Alert object and send the alert to AlertRepository
    public String createAlertObject(String trackNumber, String alertMessage, String severity, String sourceId, String eventType){
        Alert alert = new Alert(trackNumber, alertMessage, severity, LocalDateTime.now(), sourceId, eventType);
        if (alertRepository.save(alert) != null) {
            return "Success";
        }
        return "Failure";
    }

    // Retrieve all alerts
    public List<Alert> getAllAlerts() {
        List <Alert> listOfAlerts = new ArrayList<>();
        alertRepository.findAll().forEach(listOfAlerts::add);
        return listOfAlerts;
    }
}

