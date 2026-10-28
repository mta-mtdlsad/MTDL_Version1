package com.simulator;

import com.domain.Track;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrackSimulator {
    private List<Track> tracks = new ArrayList<>(List.of(
            //valid
            new Track(1, "TRK001", 80, 170, 600, "FRIEND", "ACTIVE", LocalDateTime.of(2026, 10, 27, 9, 14, 0)),
            //valid
            new Track(2, "TRK002", 80, 170, 1500, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 10, 27, 9, 14, 0)),
            //valid
            new Track(3, "TRK003", 80, 170, 1300, "HOSTILE", "ACTIVE", LocalDateTime.of(2025, 10, 27, 9, 14, 0)),
            //not valid :: latitude
            new Track(2, "TRK002", 100, 160, 500, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 27, 10, 13, 0)),
            //not valid :: longitude
            new Track(3, "TRK003", 40, 200, 550, "NEUTRAL", "INACTIVE", LocalDateTime.of(2025, 6, 27, 11, 15, 0)),//not valid :: latitude
            //not valid :: null track number
            new Track(4, null, 45, 130, 120, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 22, 1, 23, 0)),
            //not valid :: empty track number
            new Track(4, "", 45, 130, 120, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 22, 1, 23, 0)),
            //not valid :: track number < 5 char
            new Track(4, "T004", 45, 130, 120, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 22, 1, 23, 0)),
            //not valid :: speed
            new Track(5, "TRK005", 55, 120, 3000, "FRIEND", "ACTIVE", LocalDateTime.of(2022, 5, 4, 10, 33, 0)),
            //not valid :: Identity_type
            new Track(6, "TRK006", 74, 100, 1500, "TEST", "ACTIVE", LocalDateTime.of(2026, 9, 3, 3, 55, 0)),
            //not valid :: track_status
            new Track(7, "TRK007", 62, 75, 100, "FRIEND", "LOST", LocalDateTime.of(2026, 8, 18, 5, 36, 0)),
            //not valid :: created_time null
            new Track(8, "TRK008", 50, 120, 250, "HOSTILE", "ACTIVE", null),
            //not valid :: multiple (latitude, longitude, speed, identity)
            new Track(9, "TRK009", 100, 200, 5000, "TEST", "ACTIVE", LocalDateTime.of(2026, 6, 27, 10, 13, 0))
            ));

    public List<Track> getTracks(){
        return tracks;
    }
}
