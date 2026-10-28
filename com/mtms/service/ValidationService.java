package com.mtms.service;

import com.mtms.constants.MTMSConstants;
import com.mtms.domain.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationService {
    public List<String> getValidationErrors(Track track) {
        List<String> errors = new ArrayList<>();

        // TC-02 & TC-03: if track number not accepted
        if (track.track_number == null || track.track_number.trim().isEmpty()) {
            errors.add("Track Number Cannot Be Empty Alert");
        } else if (track.track_number.trim().length() < 5) {
            errors.add("Track Number Too Short Alert");
        }

        // TC-04: if latitude not accepted
        if (track.latitude < -90.0 || track.latitude > 90.0) {
            errors.add("latitude Alert");
        }

        // TC-05: if longitude not accepted
        if (track.longitude < -180.0 || track.longitude > 180.0) {
            errors.add("longitude Alert");
        }

        // TC-06 & TC-07: if speed not accepted
        if (track.speed == 0.0) {
            errors.add("speed Alert");
        } else if (track.speed < 0.0 || track.speed > 2000.0) {
            errors.add("allowed-limit Alert");
        }

        // TC-08: if identity not accepted
        List<String> validIdentities = Arrays.asList(MTMSConstants.trackFRIEND, MTMSConstants.trackHOSTILE,
                MTMSConstants.trackNEUTRAL, MTMSConstants.trackUNKNOWN);
        if (track.identity_type == null || !validIdentities.contains(track.identity_type.toUpperCase())) {
            errors.add("identity Alert");
        }

        // TC-09: if status not accepted
        if (track.track_status == null || (!track.track_status.equalsIgnoreCase(MTMSConstants.trackACTIVE) &&
                !track.track_status.equalsIgnoreCase(MTMSConstants.trackINACTIVE))) {
            errors.add("status Alert");
        }

        return errors;
    }
}
