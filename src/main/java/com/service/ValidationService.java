package com.service;

import com.domain.Track;
import com.domain.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {
    // 1. Source ID: 2 to 30 permitted characters.
    public static ValidationResult validateSource_id(Track track){
        String sourceId = track.getSourceId();
        if(sourceId == null || !sourceId.matches("[A-Za-z0-9_-]{2,30}")){
            return new ValidationResult("Invalid source_id: not matching [A-Za-z0-9_-]{2,30}", "LOW");
        }
        return null;
    }

    public static ValidationResult validateSequence_number(Track track){
        if (track.getSequenceNumber()<= 0) {
            return new ValidationResult("Invalid sequence number: is negative", "MEDIUM");
        }
        return null;
    }

    // 3. Track number: keep the existing minimum length.
    public static ValidationResult validateTrack_number(Track track) {

        String number = track.getTrackNumber();

        if (number == null || number.length() < 5) {

            return new ValidationResult(
                    "Track number must have at least 5 characters",
                    "MEDIUM"
            );
        }

        return null;
    }

    // 4. Latitude: -90 to 90 degrees.
    public static ValidationResult validateLatitude(Track track) {

        double latitude = track.getLatitude();

        if (!Double.isFinite(latitude) ||
                latitude < -90 || latitude > 90) {

            return new ValidationResult(
                    "Latitude must be between -90 and 90",
                    "MEDIUM"
            );
        }

        return null;
    }

    // 5. Longitude: -180 to 180 degrees.
    public static ValidationResult validateLongitude(Track track) {

        double longitude = track.getLongitude();

        if (!Double.isFinite(longitude) ||
                longitude < -180 || longitude > 180) {

            return new ValidationResult(
                    "Longitude must be between -180 and 180",
                    "MEDIUM"
            );
        }

        return null;
    }

    // 6. Speed: greater than 0 and at most 2000.
    public static ValidationResult validateSpeed(Track track) {

        double speed = track.getSpeed();

        if (!Double.isFinite(speed) ||
                speed <= 0 || speed > 2000) {

            return new ValidationResult(
                    "Speed must be greater than 0 and <= 2000",
                    "MEDIUM"
            );
        }

        return null;
    }

    // 7. Identity must match an allowed value.
    public static ValidationResult validateIdentity_type(Track track) {

        String identity = track.getIdentityType();

        if (!List.of(
                "FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN"
        ).contains(identity)) {

            return new ValidationResult(
                    "Invalid identity type",
                    "HIGH"
            );
        }

        return null;
    }

    // 8. Status must be ACTIVE or INACTIVE.
    public static ValidationResult validateTrack_status(Track track) {

        String status = track.getTrackStatus();

        if (!List.of(
                "ACTIVE", "INACTIVE"
        ).contains(status)) {

            return new ValidationResult(
                    "Invalid Track status",
                    "HIGH"
            );
        }

        return null;
    }

    // 9. Observation time must exist.
    public static ValidationResult validateCreated_time(Track track) {

        if (track.getCreatedTime() == null) {

            return new ValidationResult( "Observation time is missing",  "MEDIUM"
            );
        }

        return null;
    }

    // Validate every field and collect all errors.
    public static List<ValidationResult> validateTrack(Track track) {

        List<ValidationResult> errors = new ArrayList<>();

        // Include all nine checks.
        ValidationResult[] checks = {
                validateSource_id(track),
                validateSequence_number(track),
                validateTrack_number(track),
                validateLatitude(track),
                validateLongitude(track),
                validateSpeed(track),
                validateIdentity_type(track),
                validateTrack_status(track),
                validateCreated_time(track)
        };

        // Add only failed validation results.
        for (ValidationResult result : checks) {

            if (result != null) {
                errors.add(result);
            }
        }
        return errors;
    }
}
