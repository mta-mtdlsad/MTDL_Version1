package com.service;

import com.domain.Track;
import com.domain.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidationService {

    //Validating the track_number is not null. not empty, and more than or equal 5 characters
    public static ValidationResult validateTrack_number(Track track){

        if(track.getTrack_number() == null) {
            return new ValidationResult("Invalid track_number: is null", "MEDIUM");
        }
        else if(track.getTrack_number().isEmpty()) {
            return new ValidationResult("Invalid track_number: is empty", "MEDIUM");
        }
        else if(track.getTrack_number().length() < 5) {
            return new ValidationResult("Invalid track_number: shorter than five characters", "MEDIUM");
        }
        return null;
    }

    //Validate the latitude is between -90 and 90
    public static ValidationResult validateLatitude(Track track){
        double latitude = track.getLatitude();

        if(latitude>=-90 && latitude<=90) {
            return null;
        }
        return new ValidationResult("Invalid latitude: " + latitude, "LOW");
    }

    //Validate the longitude is between -180 and 180
    public static ValidationResult validateLongitude(Track track){
        double longitude = track.getLongitude();

        if(longitude>=-180 && longitude<=180) {
            return null;
        }
        return new ValidationResult("Invalid longitude: " + longitude, "LOW");
    }

    //Validate the speed is between 0 and 2000
    public static ValidationResult validateSpeed(Track track){
        double speed = track.getSpeed();

        if(speed>0 && speed<=2000) {
            return null;
        }
        return new ValidationResult("Invalid speed: " + speed, "LOW");
    }

    //Validate the identity_type is HOSTILE|FRIEND|NEUTRAL|UNKNOWN
    public static ValidationResult validateIdentity_type(Track track){
        String identity_type = track.getIdentity_type();
        List<String> validIdentities = List.of(
                "FRIEND",
                "HOSTILE",
                "NEUTRAL",
                "UNKNOWN"
        );

        if(validIdentities.contains(identity_type)) {
            return null;
        }
        return new ValidationResult("Invalid identity type: " + identity_type, "HIGH");
    }

    //Validate the track_status is either ACTIVE or INACTIVE
    public static ValidationResult validateTrack_status(Track track){
        String track_status = track.getTrack_status();
        List<String> validStatus = List.of(
                "ACTIVE",
                "INACTIVE"
        );

        if(validStatus.contains(track_status)) {
            return null;
        }
        return new ValidationResult("Invalid track status: " + track_status, "HIGH");
    }

    //Validate the created_time is not null
    public static ValidationResult validateCreated_time(Track track){

        if(track.getCreated_time() == null) {
            return new ValidationResult("Invalid created_time: is null", "MEDIUM");
        }
        return null;
    }

    //Returning the list of all the errors of a track
    public static List<ValidationResult> validateTrack(Track track){
        List<ValidationResult> results = new ArrayList<>();

        results.add(validateTrack_number(track));
        results.add(validateLatitude(track));
        results.add(validateLongitude(track));
        results.add(validateSpeed(track));
        results.add(validateIdentity_type(track));
        results.add(validateTrack_status(track));
        results.add(validateCreated_time(track));

        results.removeIf(Objects::isNull);

        return results;
    }
}
