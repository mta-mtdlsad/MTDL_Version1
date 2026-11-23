package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {
    private final PlatformGeofenceService platformGeofenceService;

    /*
     * Constructs ValidationService with PlatformGeofenceService for terrain checks.
     *
     * @param platformGeofenceService Service used to verify land/water positions.
     */
    public ValidationService(PlatformGeofenceService platformGeofenceService) {
        this.platformGeofenceService = platformGeofenceService;
    }

    // 1. Source ID: 2 to 30 permitted characters.
    /*
     * Validates that sourceId is non-null and consists of 2 to 30 alphanumeric,
     *      underscore, or hyphen characters.
     *
     * @param track The Track entity to validate.
     * @return A LOW-severity ValidationResult if invalid, or null if valid.
     */
    public ValidationResult validateSourceId(Track track){
        String sourceId = track.getSourceId();
        if(sourceId == null || !sourceId.matches("[A-Za-z0-9_-]{2,30}")){
            return new ValidationResult("Invalid source_id: not matching [A-Za-z0-9_-]{2,30}", "LOW");
        }
        return null;
    }

    // 2. Sequence Number: greater than 0
    /*
     * Validates that the track's sequence number is strictly greater than 0.
     *
     * @param track The Track entity to validate.
     * @return A MEDIUM-severity ValidationResult if <= 0, or null if valid.
     */
    public ValidationResult validateSequenceNumber(Track track){
        if (track.getSequenceNumber()<= 0) {
            return new ValidationResult("Invalid sequence number: is negative", "MEDIUM");
        }
        return null;
    }

    // 3. Track number: keep the existing minimum length.
    /*
     * Validates that trackNumber is non-null and at least 5 characters long.
     *
     * @param track The Track entity to validate.
     * @return A MEDIUM-severity ValidationResult if invalid, or null if valid.
     */
    public ValidationResult validateTrackNumber(Track track) {
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
    /*
     * Validates that latitude is a finite number between -90 and 90 degrees inclusive.
     *
     * @param track The Track entity to validate.
     * @return A MEDIUM-severity ValidationResult if out of range, or null if valid.
     */
    public ValidationResult validateLatitude(Track track) {
        double latitude = track.getLatitude();
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            return new ValidationResult(
                    "Latitude must be between -90 and 90",
                    "MEDIUM"
            );
        }
        return null;
    }

    // 5. Longitude: -180 to 180 degrees.
    /*
     * Validates that longitude is a finite number between -180 and 180 degrees inclusive.
     *
     * @param track The Track entity to validate.
     * @return A MEDIUM-severity ValidationResult if out of range, or null if valid.
     */
    public ValidationResult validateLongitude(Track track) {
        double longitude = track.getLongitude();
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            return new ValidationResult(
                    "Longitude must be between -180 and 180",
                    "MEDIUM"
            );
        }
        return null;
    }

    // 6. Speed: greater than 0 and at most 2000.
    /*
     * Validates that speed is a finite number greater than 0 and less than or equal to 2000.
     *
     * @param track The Track entity to validate.
     * @return A MEDIUM-severity ValidationResult if invalid, or null if valid.
     */
    public ValidationResult validateSpeed(Track track) {
        double speed = track.getSpeed();
        if (!Double.isFinite(speed) || speed <= 0 || speed > 2000) {
            return new ValidationResult(
                    "Speed must be greater than 0 and <= 2000",
                    "MEDIUM"
            );
        }
        return null;
    }

    // 7. Identity must match an allowed value.
    /*
     * Validates that identityType is one of FRIEND, HOSTILE, NEUTRAL, or UNKNOWN.
     *
     * @param track The Track entity to validate.
     * @return A HIGH-severity ValidationResult if invalid, or null if valid.
     */
    public ValidationResult validateIdentityType(Track track) {
        String identity = track.getIdentityType();
        if (!List.of("FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN").contains(identity)) {
            return new ValidationResult(
                    "Invalid identity type",
                    "HIGH"
            );
        }
        return null;
    }

    // 8. Status must be ACTIVE or INACTIVE.
    /*
     * Validates that trackStatus is either ACTIVE or INACTIVE.
     *
     * @param track The Track entity to validate.
     * @return A HIGH-severity ValidationResult if invalid, or null if valid.
     */
    public ValidationResult validateTrackStatus(Track track) {
        String status = track.getTrackStatus();
        if (!List.of("ACTIVE", "INACTIVE").contains(status)) {
            return new ValidationResult(
                    "Invalid Track status",
                    "HIGH"
            );
        }
        return null;
    }

    // 9. Observation time must exist.
    /*
     * Validates that the observation timestamp (createdTime) is not null.
     *
     * @param track The Track entity to validate.
     * @return A MEDIUM-severity ValidationResult if null, or null if valid.
     */
    public ValidationResult validateCreatedTime(Track track) {
        if (track.getCreatedTime() == null) {
            return new ValidationResult( "Observation time is missing",  "MEDIUM"
            );
        }
        return null;
    }

    // Validate every field and collect all errors.
    /*
     * Validates that a track's GPS coordinates match its platform type (LAND must be on land,
     * SEA must be on water). Skips validation if coordinates are invalid or type is AIR/UNKNOWN.
     *
     * @param track The Track entity to validate.
     * @return A HIGH-severity ValidationResult if the platform is in the
     *      wrong terrain, or null if valid.
     */
    public ValidationResult validatePlatformPosition(Track track) {
        if (!Double.isFinite(track.getLatitude()) || track.getLatitude() < -90 || track.getLatitude() > 90
                || !Double.isFinite(track.getLongitude()) || track.getLongitude() < -180 || track.getLongitude() > 180) {
            return null;
        }
        String trackType = track.getTrackType();
        String normalizedType = trackType == null ? "" : trackType.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        if (normalizedType.isEmpty() || normalizedType.equals("UNKNOWN")
                || normalizedType.equals("AIR") || normalizedType.equals("AIRCRAFT")
                || normalizedType.equals("AERIAL") || normalizedType.equals("PLANE")) {
            // The application has no authoritative airspace polygon or altitude field.
            return null;
        }
        if (!platformGeofenceService.isAllowedPoint(trackType, track.getLatitude(), track.getLongitude())) {
            String expectedArea = switch (normalizedType) {
                case "LAND", "LAND_VEHICLE", "GROUND_VEHICLE", "VEHICLE" -> "land";
                case "SEA", "SHIP", "VESSEL", "MARITIME", "BOAT" -> "water";
                default -> null;
            };
            if (expectedArea != null) {
                return new ValidationResult("Track position does not match platform type; expected " + expectedArea, "HIGH");
            }
        }
        return null;
    }

    /*
     * Runs all ten field and geofence validation checks on a
     *      Track and collects any validation errors.
     *
     * @param track The Track entity to validate.
     * @return A list of ValidationResult objects for all failed checks
     *      (empty list if completely valid).
     */
    public List<ValidationResult> validateTrack(Track track) {
        List<ValidationResult> errors = new ArrayList<>();

        // Include all nine checks.
        ValidationResult[] checks = {
                validateSourceId(track),
                validateSequenceNumber(track),
                validateTrackNumber(track),
                validateLatitude(track),
                validateLongitude(track),
                validateSpeed(track),
                validateIdentityType(track),
                validateTrackStatus(track),
                validateCreatedTime(track),
                validatePlatformPosition(track)
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
