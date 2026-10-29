package com.simulator;

import com.domain.Track;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Manual test tracks to test out different test cases
public class TrackSimulator {
    private List<Track> tracks = new ArrayList<>(List.of(
            //valid
            new Track("TRK001", 80, 170, 600, "FRIEND", "ACTIVE", LocalDateTime.of(2026, 1, 11, 1, 14, 0)),
            //valid
            new Track("TRK002", 90, 150, 1500, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 4, 22, 3, 14, 0)),
            //valid
            new Track("TRK003", 60, 155, 1300, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 8, 4, 4, 14, 0)),
            //valid
            new Track("TRK004", 90, 160, 700, "NEUTRAL", "ACTIVE", LocalDateTime.of(2026, 10, 14, 7, 14, 0)),
            //valid
            new Track("TRK005", 70, 180, 400, "FRIEND", "INACTIVE", LocalDateTime.of(2025, 1, 15, 12, 14, 0)),
            //valid
            new Track("TRK006", 80, 170, 1200, "FRIEND", "INACTIVE", LocalDateTime.of(2025, 3, 3, 14, 14, 0)),
            //valid
            new Track("TRK007", 60, 150, 450, "NEUTRAL", "INACTIVE", LocalDateTime.of(2025, 5, 1, 16, 14, 0)),
            //valid
            new Track("TRK008", 60, 170, 1200, "UNKNOWN", "INACTIVE", LocalDateTime.of(2025, 11, 24, 21, 14, 0)),
            //not valid :: latitude
            new Track("TRK002", 100, 160, 500, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 27, 10, 13, 0)),
            //not valid :: longitude
            new Track("TRK003", 40, 200, 550, "NEUTRAL", "INACTIVE", LocalDateTime.of(2025, 6, 27, 11, 15, 0)),
            //not valid :: null track number
            new Track(null, 45, 130, 120, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 22, 1, 23, 0)),
            //not valid :: empty track number
            new Track("", 45, 130, 120, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 22, 1, 23, 0)),
            //not valid :: track number < 5 char
            new Track("T004", 45, 130, 120, "HOSTILE", "ACTIVE", LocalDateTime.of(2026, 6, 22, 1, 23, 0)),
            //not valid :: speed
            new Track("TRK005", 55, 120, 3000, "FRIEND", "ACTIVE", LocalDateTime.of(2022, 5, 4, 10, 33, 0)),
            //not valid :: Identity_type
            new Track("TRK006", 74, 100, 1500, "TEST", "ACTIVE", LocalDateTime.of(2026, 9, 3, 3, 55, 0)),
            //not valid :: track_status
            new Track("TRK007", 62, 75, 100, "FRIEND", "LOST", LocalDateTime.of(2026, 8, 18, 5, 36, 0)),
            //not valid :: created_time null
            new Track("TRK008", 50, 120, 250, "HOSTILE", "ACTIVE", null),
            //not valid :: multiple (latitude, longitude, speed, identity)
            new Track("TRK009", 100, 200, 5000, "TEST", "ACTIVE", LocalDateTime.of(2026, 6, 27, 10, 13, 0))
    ));

    public List<Track> getTracks(){
        return tracks;
    }

    public List<Track> getTracksEnhanced(){
        Random random = new Random();
        List<Track> tracks = new ArrayList<>();
        String[] identity_types = {"FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN"};
        String[] track_status = {"ACTIVE", "INACTIVE"};
        double latitude, longitude, speed;
        String track_number;
        int day = 1, hour;
        for(int i = 1; i <= 100; i++) {
            track_number = "TRK00" + i;
            latitude = 10.0 + (random.nextInt(100) / 100.0);
            longitude = 70.0 + (random.nextInt(100) / 100.0);
            speed = 300.0 + (random.nextInt(1500));
            hour = i % 24;
            if(hour == 0) day++;
            tracks.add(new Track(track_number, latitude, longitude, speed, identity_types[random.nextInt(4)], track_status[random.nextInt(2)], LocalDateTime.of(2026, 1, day, hour, 0, 0)));
        }
        return tracks;
    }
}
