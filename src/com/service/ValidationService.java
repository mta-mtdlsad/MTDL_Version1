package com.service;

import com.domain.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidationService {

    public static String validateTrack_number(Track track){

        if(track.getTrack_number() == null) {
            return "Invalid track_number: is null";
        }
        else if(track.getTrack_number().isEmpty()) {
            return "Invalid track_number: is empty";
        }
        else if(track.getTrack_number().length() < 5) {
            return "Invalid track_number: shorter than five characters";
        }
        return null;
    }

    public static String validateLatitude(Track track){
        double latitude = track.getLatitude();

        if(latitude>=-90 && latitude<=90) {
            return null;
        }
        return "Invalid latitude: " + latitude;
    }

    public static String validateLongitude(Track track){
        double longitude = track.getLongitude();

        if(longitude>=-180 && longitude<=180) {
            return null;
        }
        return "Invalid longitude: " + longitude;
    }

    public static String validateSpeed(Track track){
        double speed = track.getSpeed();

        if(speed>0 && speed<=2000) {
            return null;
        }
        return "Invalid speed: " + speed;
    }

    public static String validateIdentity_type(Track track){
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
        return "Invalid identity type: " + identity_type;
    }

    public static String validateTrack_status(Track track){
        String track_status = track.getTrack_status();
        List<String> validStatus = List.of(
                "ACTIVE",
                "INACTIVE"
        );

        if(validStatus.contains(track_status)) {
            return null;
        }
        return "Invalid track status: " + track_status;
    }

    public static String validateCreated_time(Track track){

        if(track.getCreated_time() == null) {
            return "Invalid created_time: is null";
        }
        return null;
    }

    public static List<String> validateTrack(Track track){
        List<String> results = new ArrayList<>();

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
