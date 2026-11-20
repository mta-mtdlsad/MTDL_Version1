package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.util.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
public class MessageParserService {
    @Autowired
    private AlertService alert_service;

    // Returns Track if message isn't malformed
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
        if (fields.length != 10) {
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
            String trackType = fields[3];
            double latitude = Double.parseDouble(fields[4]);
            double longitude = Double.parseDouble(fields[5]);
            double speed = Double.parseDouble(fields[6]);
            String identity = fields[7];
            String status = fields[8];
            LocalDateTime createdTime = LocalDateTime.parse(fields[9]);
            track = new Track(sourceId, sequence, trackNumber, trackType, latitude, longitude, speed, identity, status, createdTime);

        } catch (NumberFormatException e) {
            rejectMalformed(trackNumber, "Malformed message: Invalid number format");
            throw new IllegalArgumentException(e);
        } catch (DateTimeParseException e) {
            rejectMalformed(trackNumber, "Malformed message: Invalid observation time");
            throw new IllegalArgumentException(e);
        }
        return track;
    }

    // Create an alert for malformed messages.
    private void rejectMalformed(String trackNumber, String message) {
        ValidationResult result = new ValidationResult(message, "HIGH");
        saveAlert(trackNumber, result);
        throw new IllegalArgumentException(message);
    }

    // Save malformed alerts without a complete Track.
    private void saveAlert(String trackNumber, ValidationResult result) {
        try {
            alert_service.createAlert(trackNumber, result);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println(
                    "Alert creation failed: " + e.getMessage()
            );
        }
    }
}
