package com.network;

import java.time.LocalDateTime;
// Represents one observation received through UDP

public record UDPMessage(
        String sourceId,       // Source sending the message
        Integer sequenceNumber,     // Message sequence number
        String trackNumber,     // Track identifier
        double latitude,       // Latitude position
        double longitude,      // Longitude position
        double speed,        // Track speed
        String identity,       // Friend, hostile, etc.
        String status,        // Track status
        LocalDateTime observationTime // Time of observation
) {

}