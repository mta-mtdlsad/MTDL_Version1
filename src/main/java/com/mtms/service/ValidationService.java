package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Track;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    private static final Pattern SOURCE_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{2,30}$");
    private static final List<String> VALID_IDENTITIES = Arrays.asList(
            MTMSConstants.TRACK_FRIEND,
            MTMSConstants.TRACK_HOSTILE,
            MTMSConstants.TRACK_NEUTRAL,
            MTMSConstants.TRACK_UNKNOWN
    );

    public ValidationService() {}

    public List<String> getValidationErrors(Track track) {
        List<String> errors = new ArrayList<>();

        if (track == null) {
            errors.add("Processing Failure: Track Payload Cannot Be Null Alert");
            return errors;
        }

        if (track.getSourceId() == null || track.getSourceId().trim().isEmpty()) {
            errors.add("Processing Failure: Source ID Cannot Be Empty Alert");
        } else if (!SOURCE_ID_PATTERN.matcher(track.getSourceId().trim()).matches()) {
            errors.add("Processing Failure: Source ID Invalid Format Alert");
        }

        if (track.getSequenceNumber() == null) {
            errors.add("Processing Failure: Sequence Number Cannot Be Null Alert");
        } else if (track.getSequenceNumber() < 0) {
            errors.add("Processing Failure: Sequence Number Cannot Be Negative Alert");
        }

        if (track.getTrackNumber() == null || track.getTrackNumber().trim().isEmpty()) {
            errors.add("Processing Failure: Track Number Cannot Be Empty Alert");
        } else if (track.getTrackNumber().trim().length() < 5) {
            errors.add("Processing Failure: Track Number Too Short Alert");
        }

        if (track.getLatitude() < -90.0 || track.getLatitude() > 90.0) {
            errors.add("Processing Failure: latitude Alert");
        }

        if (track.getLongitude() < -180.0 || track.getLongitude() > 180.0) {
            errors.add("Processing Failure: longitude Alert");
        }

        if (track.getSpeed() == 0.0) {
            errors.add("Processing Failure: speed Alert");
        } else if (track.getSpeed() < 0.0 || track.getSpeed() > 2000.0) {
            errors.add("Processing Failure: allowed-limit Alert");
        }

        if (track.getIdentityType() == null || !VALID_IDENTITIES.contains(track.getIdentityType().trim().toUpperCase())) {
            errors.add("Processing Failure: identity Alert");
        }

        if (track.getTrackStatus() == null || (!track.getTrackStatus().equalsIgnoreCase(MTMSConstants.TRACK_ACTIVE) &&
                !track.getTrackStatus().equalsIgnoreCase(MTMSConstants.TRACK_INACTIVE))) {
            errors.add("Processing Failure: status Alert");
        }

        return errors;
    }
}
