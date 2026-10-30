package com.mtms.service;

import com.mtms.domain.Alert;
import com.mtms.repository.AlertRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class AlertService {

    static AlertRepository alertRepository = new AlertRepository();

    // Choose severity based on track identity
    private static String chooseSeverity(String identityType) {
        if (identityType == null) {
            throw new IllegalArgumentException(
                    "Identity type cannot be null");
        }
        return switch (identityType.toUpperCase(Locale.ROOT)) {
            case "HOSTILE", "UNKNOWN"  -> "HIGH";
            case "FRIEND", "NEUTRAL" -> "LOW";
            default -> "MEDIUM";
        };
    }

    // Process alert by validating the track number and adding severity
    public static void processAlert(
            String trackNumber,
            String alertMessage,
            String identityType) {

        if (alertMessage.contains("short"))
            trackNumber = "INVALID(" + trackNumber + ")";

        String severity = chooseSeverity(identityType); // Choose severity
        createAlertObject(trackNumber, alertMessage, severity); // create alert object
    }

    // Create the Alert object and send the alert to AlertRepository
    public static void createAlertObject(String trackNumber, String alertMessage, String severity){
        Alert alert = new Alert(
                trackNumber,
                alertMessage,
                severity,
                LocalDateTime.now()
        );
        AlertRepository.addAlert(alert);
    }

    // Retrieve all alerts
    public static List<Alert> getAllAlerts() {
        List <Alert> listOfAlerts = alertRepository.getAllAlerts();
        System.out.println("\nRetrieving all alerts: ");
        if (listOfAlerts != null)
            for (Alert alert : listOfAlerts) {
                System.out.println(alert);
            }
        else
            System.out.println("No available alerts");
        return listOfAlerts;
    }
}


