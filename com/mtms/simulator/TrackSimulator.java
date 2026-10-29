package com.mtms.simulator;

import com.mtms.domain.Track;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TrackSimulator {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //Req FR-01
    public List<Track> generateTracks() {
        List<Track> simulatedFeed = new ArrayList<>();
        String now = LocalDateTime.now().format(formatter);

        // TC-01: Valid Track --> Rejected
        simulatedFeed.add(new Track(0, "TRK1001", 34.0522, -118.2437,
                650.00, "FRIEND", "ACTIVE", now));

        // TC-02: Blank Track number --> Rejected
        simulatedFeed.add(new Track(0, "   ", 34.0522, -118.2437,
                500.00, "FRIEND", "ACTIVE", now));

        // TC-03: Short Track number --> Rejected
        simulatedFeed.add(new Track(0, "TRK", 34.0522, -118.2437,
                500.00, "FRIEND", "ACTIVE", now));

        // TC-04: Invalid latitude --> Rejected////////////////////////////////
        simulatedFeed.add(new Track(0, "TRK1004", 95.0000, -118.2437,
                500.00, "FRIEND", "ACTIVE", now));

        // TC-05: Invalid longitude --> Rejected
        simulatedFeed.add(new Track(0, "TRK1005", 34.0522, 200.0000,
                500.00, "FRIEND", "ACTIVE", now));

        // TC-06: Zero speed --> Rejected
        simulatedFeed.add(new Track(0, "TRK1006", 34.0522, -118.2437,
                0.00, "FRIEND", "ACTIVE", now));

        // TC-07: Excess speed --> Rejected
        simulatedFeed.add(new Track(0, "TRK1007", 34.0522, -118.2437,
                2500.00, "FRIEND", "ACTIVE", now));

        // TC-08: Invalid identity --> Rejected
        simulatedFeed.add(new Track(0, "TRK1008", 34.0522, -118.2437,
                500.00, "ENEMY", "ACTIVE", now));

        // TC-09: Invalid status --> Rejected
        simulatedFeed.add(new Track(0, "TRK1009", 34.0522, -118.2437,
                500.00, "FRIEND", "RUNNING", now));

        // TC-10: Multiple failures (Bad latitude, speed and identity) --> Rejected
        simulatedFeed.add(new Track(0, "TRK1010", 120.0000, -118.2437,
                3000.00, "TERRORIST", "ACTIVE", now));

        // TC-17: SQL injection-like number --> Accepted
        simulatedFeed.add(new Track(0, "TRK1' OR '1'='1", 34.0522, -118.2437,
                500.00, "FRIEND", "ACTIVE", now));

        //Dummy Data (Valid)
        simulatedFeed.add(new Track(0, "TRK1001", 34.05, -118.24, 650.00, "FRIEND", "ACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1002", 40.71, -74.00, 350.00, "NEUTRAL", "ACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1003", 51.50, -0.12, 120.00, "FRIEND", "INACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1004", 35.67, 139.65, 850.00, "HOSTILE", "ACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1005", -33.86, 151.20, 920.00, "UNKNOWN", "ACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1006", 48.85, 2.35, 450.00, "NEUTRAL", "INACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1007", -22.90, -43.17, 1200.00, "HOSTILE", "ACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1008", 1.35, 103.81, 280.00, "FRIEND", "ACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1009", 55.75, 37.61, 710.00, "UNKNOWN", "ACTIVE", now));
        simulatedFeed.add(new Track(0, "TRK1010", -26.20, 28.04, 610.00, "NEUTRAL", "ACTIVE", now));
        //Dummy Data (Invalid)
        simulatedFeed.add(new Track(0, "   ", 10.00, 20.00, 400.00, "FRIEND", "ACTIVE", now));      // Empty Name
        simulatedFeed.add(new Track(0, "ABCD", 15.00, 25.00, 300.00, "FRIEND", "ACTIVE", now));      // Name Too Short
        simulatedFeed.add(new Track(0, "TRK999", 98.50, 45.00, 500.00, "NEUTRAL", "ACTIVE", now));   // Bad Lat (>90)
        simulatedFeed.add(new Track(0, "TRK998", -105.0, 30.00, 600.00, "FRIEND", "ACTIVE", now));   // Bad Lat (<-90)
        simulatedFeed.add(new Track(0, "TRK997", 25.00, 195.00, 200.00, "UNKNOWN", "ACTIVE", now));  // Bad Lon (>180)
        simulatedFeed.add(new Track(0, "TRK996", 30.00, -210.0, 150.00, "FRIEND", "ACTIVE", now));   // Bad Lon (<-180)
        simulatedFeed.add(new Track(0, "TRK995", 12.00, 42.00, 0.00, "HOSTILE", "ACTIVE", now));     // Speed is 0
        simulatedFeed.add(new Track(0, "TRK994", 45.00, -12.00, 2400.0, "FRIEND", "ACTIVE", now));   // Speed > 2000
        simulatedFeed.add(new Track(0, "TRK993", 8.00, 11.00, 550.00, "ALIEN", "ACTIVE", now));      // Bad Identity
        simulatedFeed.add(new Track(0, "TRK992", 19.00, -85.00, 410.00, "FRIEND", "PAUSED", now));   // Bad Status


        return simulatedFeed;
    }
}
