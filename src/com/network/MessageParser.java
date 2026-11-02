package com.network;

import com.domain.ValidationResult;
import com.repository.UDPObservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MessageParser {

    public UDPMessage parse(String message) {
        // Check if the message is empty
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is empty");
        }
        // Split the message using | as the separator
        String[] fields = message.split("\\|", -1);
        // Every UDP message must contain exactly 9 fields
        if (fields.length != 9) {
            throw new IllegalArgumentException(
                    "9 fields required, received :: " + fields.length
            );
        }
        // Remove spaces and check for missing values
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            if (fields[i].isEmpty()) {
                throw new IllegalArgumentException(
                        "Field " + (i + 1) + " is missing"
                );
            }
        }
        try {
            // Convert the text fields into their correct data types
            String sourceId = fields[0];
            Integer sequence = Integer.parseInt(fields[1]);
            String trackNumber = fields[2];
            double latitude = Double.parseDouble(fields[3]);
            double longitude = Double.parseDouble(fields[4]);
            double speed = Double.parseDouble(fields[5]);
            String identity = fields[6];
            String status = fields[7];
            LocalDateTime observationTime = LocalDateTime.parse(fields[8]);

            if(!sourceId.matches("[A-Za-z0-9_-]{2,30}")){
                throw new IllegalArgumentException(
                        "Invalid Source ID"
                );
            }
            if(trackNumber == null || trackNumber.isEmpty() || trackNumber.length() < 5) {
                throw new IllegalArgumentException(
                        "Track number cannot be NULL | EMPTY | less than 5 characters"
                );
            }
            // Check the sequence number
            if (sequence < 0) {
                throw new IllegalArgumentException(
                        "Sequence number cannot be negative"
                );
            }
            // Validate latitude
            if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("Invalid latitude");
            }
            // Validate longitude
            if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Invalid longitude");
            }
            // Validate speed
            if (!Double.isFinite(speed) || speed < 0 || speed > 2000) {
                throw new IllegalArgumentException("Invalid speed");
            }
            // Validate identity
            List<String> validIdentities = List.of(
                    "FRIEND",
                    "HOSTILE",
                    "NEUTRAL",
                    "UNKNOWN"
            );
            if(!validIdentities.contains(identity)) {
                throw new IllegalArgumentException("Invalid identity");
            }
            // Validate status
            List<String> validStatus = List.of(
                    "ACTIVE",
                    "INACTIVE"
            );
            if(!validStatus.contains(status)) {
                throw new IllegalArgumentException("Invalid status");
            }

            // Create and return the UDP message object
            return new UDPMessage(
                    sourceId,
                    sequence,
                    trackNumber,
                    latitude,
                    longitude,
                    speed,
                    identity,
                    status,
                    observationTime
            );

        } catch (NumberFormatException | DateTimeParseException e) {
            // Reject messages containing invalid numbers or dates
            throw new IllegalArgumentException(
                    "Invalid number or date-time format", e
            );
        }
    }
}
