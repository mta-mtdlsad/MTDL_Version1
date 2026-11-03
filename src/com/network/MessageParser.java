package com.network;

import com.domain.Track;
import com.domain.ValidationResult;
import com.service.AlertService;
import com.service.TrackService;
import com.service.ValidationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MessageParser {

    //object of the track service
    private final TrackService trackService = new TrackService();
    //object for the alert service
    private final AlertService alertService = new AlertService();

    public Track parse(String message) {
        // Check if the message is empty
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is empty");
        }
        // Split the message using | as the separator
        String[] fields = message.split("\\|", -1);

        // Every UDP message must contain exactly 9 fields
        if (fields.length != 9) {
            ValidationResult result = new ValidationResult("Malformed message: 9 fields required, received :: " + fields.length, "HIGH");
            System.out.println(result.toString());
            alertService.createAlert(null, result);
            throw new IllegalArgumentException(
                    "9 fields required, received :: " + fields.length
            );
        }

        // Remove spaces and check for missing values
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            if (fields[i].isEmpty()) {
                ValidationResult result = new ValidationResult("Malformed message: Field " + (i + 1) + " is missing", "HIGH");
                System.out.println(result.toString());
                alertService.createAlert(null, result);
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

            //create track using the fields
            Track track = new Track(sourceId, sequence, trackNumber, latitude, longitude, speed, identity, status, createdTime);

            // STEP 6: Validate the Track only once.
            List<ValidationResult> errors = ValidationService.validateTrack(track);

            // If any rule fails, reject the message.
            if (!errors.isEmpty()) {
                // Show all validation errors in one message.
                for(ValidationResult result: errors){
                    alertService.createAlert(track,result);
                    System.out.println(result.toString());
                }
                throw new IllegalArgumentException(
                        "Invalid Track: " + errors
                );
            }

            return track;
        } catch (NumberFormatException e) {
            ValidationResult result = new ValidationResult("Invalid number in UDP message", "HIGH");
            alertService.createAlert(null, result);
            throw new IllegalArgumentException("Invalid number in UDP message: " +
                    e.getMessage(), e
            );

        } catch (DateTimeParseException e) {
            ValidationResult result = new ValidationResult("Invalid observation time. Expected " + "yyyy-MM-dd'T'HH:mm:ss", "HIGH");
            alertService.createAlert(null, result);
            throw new IllegalArgumentException("Invalid observation time. Expected " + "yyyy-MM-dd'T'HH:mm:ss", e
            );
        }
    }
}


