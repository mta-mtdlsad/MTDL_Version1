package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Track;
import com.mtms.domain.TrackType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
public class MessageParserService {

    public static class ParseResult {
        public final Track track;
        public final String errorMessage;
        public final String trackNumber;
        public final String sourceId;

        public ParseResult(Track track, String errorMessage, String trackNumber, String sourceId) {
            this.track = track;
            this.errorMessage = errorMessage;
            this.trackNumber = trackNumber;
            this.sourceId = sourceId;
        }
    }

    public ParseResult parse(String payload) {
        String[] fields = payload.split("\\|", -1);

        if (fields.length != 9 && fields.length != 10) {
            return new ParseResult(
                    null,
                    "Malformed message: Expected 9 fields (legacy) or 10 fields with platform type but received " + fields.length,
                    null,
                    null
            );
        }

        String sourceId = fields[0] != null ? fields[0].trim() : null;
        String trackNumber = fields[2] != null ? fields[2].trim() : null;

        try {
            Track track = new Track();
            track.setSourceId(sourceId);
            track.setSequenceNumber(fields[1].trim().isEmpty() ? null : Integer.parseInt(fields[1].trim()));
            track.setTrackNumber(trackNumber);
            track.setLatitude(fields[3].trim().isEmpty() ? 0.0 : Double.parseDouble(fields[3].trim()));
            track.setLongitude(fields[4].trim().isEmpty() ? 0.0 : Double.parseDouble(fields[4].trim()));
            track.setSpeed(fields[5].trim().isEmpty() ? 0.0 : Double.parseDouble(fields[5].trim()));
            track.setIdentityType(fields[6] != null ? fields[6].trim() : null);
            track.setTrackStatus(fields[7] != null ? fields[7].trim() : null);
            track.setPlatformType(TrackType.fromValue(fields.length == 10 ? fields[9] : "UNKNOWN"));

            String rawTime = fields[8] != null ? fields[8].trim().replace("T", " ") : "";
            LocalDateTime parsedTime = rawTime.isEmpty()
                    ? LocalDateTime.now()
                    : LocalDateTime.parse(rawTime, MTMSConstants.TIMESTAMP_FORMATTER);
            track.setCreatedTime(parsedTime);

            return new ParseResult(track, null, trackNumber, sourceId);
        } catch (NumberFormatException e) {
            return new ParseResult(
                    null,
                    "Value type conversion exception: Numeric fields contain invalid formats.",
                    trackNumber,
                    sourceId
            );
        } catch (DateTimeParseException e) {
            return new ParseResult(
                    null,
                    "Timestamp parse exception: Expected yyyy-MM-dd HH:mm:ss format.",
                    trackNumber,
                    sourceId
            );
        }
    }
}
