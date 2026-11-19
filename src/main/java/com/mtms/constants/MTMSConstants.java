package com.mtms.constants;

import java.time.format.DateTimeFormatter;

public final class MTMSConstants {
    private MTMSConstants() {}

    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Alert Severity Constants
    public static final String ALERT_LOW = "LOW";
    public static final String ALERT_MID = "MEDIUM";
    public static final String ALERT_HIGH = "HIGH";

    // Track Operational Status Constants
    public static final String TRACK_ACTIVE = "ACTIVE";
    public static final String TRACK_INACTIVE = "INACTIVE";

    // Track Identity Classification Constants
    public static final String TRACK_FRIEND = "FRIEND";
    public static final String TRACK_HOSTILE = "HOSTILE";
    public static final String TRACK_NEUTRAL = "NEUTRAL";
    public static final String TRACK_UNKNOWN = "UNKNOWN";

    // Replay Speed Delay Constants (in milliseconds)
    public static final int REPLAY_HALF = 2000;
    public static final int REPLAY_NORMAL = 1000;
    public static final int REPLAY_DOUBLE = 500;
}
