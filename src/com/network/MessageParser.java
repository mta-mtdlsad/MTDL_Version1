package com.network;

import com.domain.Track;
import com.service.TrackService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class MessageParser {

    private final TrackService trackService = new TrackService();

    public Track parse(String message) {
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
            LocalDateTime createdTime = LocalDateTime.parse(fields[8]);

            Track track = new Track(sourceId, sequence, trackNumber, latitude, longitude, speed, identity, status, createdTime);
            trackService.validateTrack(track);

            // Create and return the Track message object
            if(!trackService.validateTrack(track)){
                throw new IllegalArgumentException("Invalid track");
            }
            return track;

        } catch (NumberFormatException | DateTimeParseException e) {
            // Reject messages containing invalid numbers or dates
            throw new IllegalArgumentException(
                    "Invalid number or date-time format", e
            );
        }
    }
}
