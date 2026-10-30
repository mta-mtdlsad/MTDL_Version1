package com.mtms.service;

import com.mtms.domain.Track;
import java.util.ArrayList;
import java.util.List;

public class ValidationService {

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

        // Validation to Check Latitude (-90 to 90)
        if (track.getLatitude() <- 90 ||
                track.getLatitude() > 90)
        {
            errors.add("Latitude is Invalid ");
        }

        // Validation to Check Longitude (-180 to 180)
        if (track.getLongitude() <- 180 ||
                track.getLongitude() > 180)
        {
            errors.add("longitude is Invalid");
        }

        // Validation to Check speed (>0 and <=2000)
        if (track.getSpeed() <= 0) {
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

        //Validation to check CreationTime
        if (track.getCreatedTime() == null)
        {
            errors.add("Created Time is Invalid ");
        }

        return errors;
    }
}
