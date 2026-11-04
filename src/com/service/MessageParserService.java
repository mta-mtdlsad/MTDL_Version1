package com.service;

import com.domain.Track;
import com.domain.ValidationResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MessageParserService {
    private final AlertService alertService = new AlertService();
    public Track parse(String message) {
        // Check empty messages.
        if (message == null || message.isBlank()) {
            rejectMalformed(null, "Malformed message: Message is empty");
        }
        // Split the message into fields.
        String[] fields = message.split("\\|", -1);
        // Extract track number if available.
        String trackNumber = fields.length > 2 ? fields[2].trim() : null;
        // Check number of fields.
        if (fields.length != 9) {
            rejectMalformed(trackNumber, "Malformed message: 9 fields required, received " + fields.length);
        }
        // Check missing fields.
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            if (fields[i].isEmpty()) {
                rejectMalformed(trackNumber, "Malformed message: Field " + (i + 1) + " is missing");
            }
        }
        Track track;
        try {
            String sourceId = fields[0];
            int sequence = Integer.parseInt(fields[1]);
            trackNumber = fields[2];
            double latitude = Double.parseDouble(fields[3]);
            double longitude = Double.parseDouble(fields[4]);
            double speed = Double.parseDouble(fields[5]);
            String identity = fields[6];
            String status = fields[7];
            LocalDateTime createdTime = LocalDateTime.parse(fields[8]);
            track = new Track(sourceId, sequence, trackNumber, latitude, longitude, speed, identity, status, createdTime);

        } catch (NumberFormatException e) {
            rejectMalformed(trackNumber, "Malformed message: Invalid number format");
            throw new IllegalArgumentException(e);
        } catch (DateTimeParseException e) {
            rejectMalformed(trackNumber, "Malformed message: Invalid observation time");
            throw new IllegalArgumentException(e);
        }
        // Validate all Track fields.
        List<ValidationResult> errors = ValidationService.validateTrack(track);
        if (!errors.isEmpty()) {
            for (ValidationResult result : errors) {
                saveAlert(track, result);
            }
            throw new IllegalArgumentException("Invalid Track: " + errors);
        }
        return track;
    }
    // Create an alert for malformed messages.
    private void rejectMalformed(String trackNumber, String message) {
        ValidationResult result = new ValidationResult(message, "HIGH");
        saveAlert(trackNumber, result);
        throw new IllegalArgumentException(message);
    }

    // Save validation alerts using a Track.
    private void saveAlert(Track track, ValidationResult result) {
        try {
            alertService.createAlert(track, result);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Alert creation failed: " + e.getMessage());
        }
    }
    // Save malformed alerts without a complete Track.
    private void saveAlert(String trackNumber, ValidationResult result) {
        try {
            alertService.createAlert(trackNumber, result);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println(
                    "Alert creation failed: " + e.getMessage()
            );
        }
    }
}