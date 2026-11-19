package com.mtms.domain;

import java.util.Locale;

/** Platform category used for map symbology; distinct from identity and status. */
public enum TrackType {
    SHIP,
    AIRCRAFT,
    LAND_VEHICLE,
    UNKNOWN;

    public static TrackType fromValue(String value) {
        if (value == null || value.isBlank()) return UNKNOWN;
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (normalized) {
            case "SHIP", "VESSEL", "MARITIME", "BOAT" -> SHIP;
            case "AIR", "AIRCRAFT", "AERIAL", "PLANE" -> AIRCRAFT;
            case "LAND", "LAND_VEHICLE", "VEHICLE", "GROUND_VEHICLE" -> LAND_VEHICLE;
            case "UNKNOWN", "UNSPECIFIED" -> UNKNOWN;
            default -> UNKNOWN;
        };
    }
}
