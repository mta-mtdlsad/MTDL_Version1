package com.mtms.service;

import com.mtms.domain.Track;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationService {

    public static List<String> parseTrackString(String trackString) {
        List<String> errors = new ArrayList<>();

        // splitting string into segments
        String[] stringSeg = trackString.split("\\|");

        // checking the use of correct partitioning
        if (!trackString.contains("|")) {
            errors.add("Message format is invalid");
        }


        // checking number of segments
        if (stringSeg.length != 9) {
            errors.add("Message missing fields");
        }

        try{
            Double.parseDouble(stringSeg[3].trim()); //latitude
        } catch (Exception e) {
            errors.add("Invalid latitude format");
        }

        try{
            Double.parseDouble(stringSeg[4].trim()); //longitude
        } catch (Exception e) {
            errors.add("Invalid longitude format");
        }

        try{
            Double.parseDouble(stringSeg[5].trim()); //speed
        } catch (Exception e) {
            errors.add("Invalid speed format");
        }

        try{
            LocalDateTime.parse(stringSeg[8].trim()); //observationTime
        } catch (Exception e) {
            errors.add("Invalid observation time format");
        }

        return errors;
    }

    public static List<String> validateTrack(Track track) {
        List<String> errors = new ArrayList<>();

        // Validation to Check Track number
        if (track.getTrackNumber() == null ||
                track.getTrackNumber().trim().isEmpty()) {
            errors.add("Track Number is Empty");
        } else if (track.getTrackNumber().trim().length() < 6)
        {
            errors.add("Track Number is Too Short");
        }

        // Validation to Check Source Id
        if (track.getSourceId() == null ||
                track.getSourceId().trim().isEmpty()) {
            errors.add("Source Id is Empty");
        } else if (track.getSourceId().trim().length() < 4)
        {
            errors.add("Source Id is Too Short");
        }

        // Validation to Check speed (>0 and <=2000)
        if (track.getSequenceNumber() <= 0) {
            errors.add("Sequence Number is Invalid ");
        }

        // Validation to Check Latitude (-90 to 90)
        if (track.getLatitude() < -90 ||
                track.getLatitude() > 90)
        {
            errors.add("Latitude is Invalid ");
        }

        // Validation to Check Longitude (-180 to 180)
        if (track.getLongitude() < -180 ||
                track.getLongitude() > 180)
        {
            errors.add("longitude is Invalid");
        }

        // Validation to Check speed (>0 and <=2000)
        if (track.getSpeed() < 0) {
            errors.add("Speed is Invalid ");
        } else if (track.getSpeed() > 2000)
        {
            errors.add("Speed is too high ");
        }

        // Validation to Check identity (FRIEND, HOSTILE, UNKNOWN, NEUTRAL)
        if (track.getIdentity() == null ||
                !(track.getIdentity().equals("FRIEND") ||
                        track.getIdentity().equals("HOSTILE") ||
                        track.getIdentity().equals("UNKNOWN") ||
                        track.getIdentity().equals("NEUTRAL")))
        {
            errors.add("Invalid identity");
        }

        // Validation to Check status
        if (track.getTrackStatus() == null ||
                !(track.getTrackStatus().equals("ACTIVE") ||
                        track.getTrackStatus().equals("INACTIVE")))
        {
            errors.add("Status is Invalid ");
        }

        // Validation to check ObservationTime
        if (track.getObservationTime() == null)
        {
            errors.add("Observation Time is Invalid ");
        }

        // Validation to check ReceivedTime
        if (track.getReceivedTime() == null)
        {
            errors.add("Received Time is Invalid ");
        }

        return errors;
    }
}
