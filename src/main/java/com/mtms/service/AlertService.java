package com.mtms.service;

import com.mtms.domain.Alert;
import com.mtms.repository.AlertRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class AlertService {

    static AlertRepository alertRepository = new AlertRepository();


    // Process alert and save in repository
    public static void processAlert(String sourceId, String trackNumber, String alertMessage, String eventType) {

        // processes short track number
        if (alertMessage.contains("short"))
            trackNumber = "INVALID(" + trackNumber + ")";

        // create alert object and save to repository
        createAlertObject(
                trackNumber,
                alertMessage,
                chooseSeverity(eventType), // assigning severity base on event type
                sourceId,
                eventType);

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
    public static void createAlertObject(String trackNumber, String alertMessage, String severity, String sourceId, String eventType){
        Alert alert = new Alert(
                trackNumber,
                alertMessage,
                severity,
                LocalDateTime.now(),
                sourceId,
                eventType
        );
        AlertRepository.addAlert(alert);
    }

    // Retrieve all alerts
    public static List<Alert> getAllAlerts() {
        List <Alert> listOfAlerts = alertRepository.getAllAlerts();
//        System.out.println("\nRetrieving all alerts: ");
//        if (listOfAlerts != null)
//            for (Alert alert : listOfAlerts) {
//                System.out.println(alert);
//            }
//        else
//            System.out.println("No available alerts");
        return listOfAlerts;
    }
}


