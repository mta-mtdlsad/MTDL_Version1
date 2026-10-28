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

        // TC-04: Invalid latitude --> Rejected
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

        return simulatedFeed;
    }
}
