package com.mtms.simulator;

import com.mtms.domain.Track;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrackSimulator {

    public List<Track> generateTracks() {
        List<Track> tracks = new ArrayList<>();

        // T1 - Valid Track Number
        tracks.add(new Track(
                1,
                "TRK1001",
                25.2048,
                55.2708,
                650,
                "FRIEND",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 41, 25, 123_000_000)
        ));

        // T2 - No Track Number
        tracks.add(new Track(
                2,
                "",
                25.2048,
                55.2708,
                420,
                "FRIEND",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 26, 7, 41, 25, 123_000_000)
        ));

        // T3 - Short Track Number
        tracks.add(new Track(
                3,
                "TRK",
                25.2048,
                55.2708,
                420,
                "NEUTRAL",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 25, 7, 41, 25, 123_000_000)
        ));

        // T4 - Invalid Latitude
        tracks.add(new Track(
                4,
                "TRK004",
                95,
                55.2708,
                311,
                "HOSTILE",
                "INACTIVE",
                LocalDateTime.of(2026, 10, 22, 7, 41, 25, 123_000_000)
        ));

        // T5 - Invalid Longitude
        tracks.add(new Track(
                5,
                "TRK005",
                25.2048,
                200,
                420,
                "FRIEND",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 18, 7, 41, 25, 123_000_000)
        ));

        // T6 - Zero Speed
        tracks.add(new Track(
                6,
                "TRK006",
                25.2048,
                55.2708,
                0,
                "UNKNOWN",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 28, 2, 41, 25, 123_000_000)
        ));

        // T7 - Excess Speed
        tracks.add(new Track(
                7,
                "TRK007",
                25.2048,
                55.2708,
                2500,
                "FRIEND",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 18, 3, 41, 25, 123_000_000)
        ));

        // T8 - Invalid Identity
        tracks.add(new Track(
                8,
                "TRK008",
                25.2048,
                55.2708,
                200,
                "ENEMY",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 12, 7, 41, 25, 123_000_000)
        ));

        // T9 - Invalid Status
        tracks.add(new Track(
                9,
                "TRK009",
                25.2048,
                55.2708,
                250,
                "FRIEND",
                "RUNNING",
                LocalDateTime.of(2026, 10, 28, 5, 41, 25, 123_000_000)
        ));

        // T10 - Multiple Validation Failures
        tracks.add(new Track(
                10,
                "TRK010",
                95,
                55.2708,
                0,
                "ENEMY",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 12, 6, 41, 25, 123_000_000)
        ));

        // T11 - Valid Track Number
        tracks.add(new Track(
                11,
                "TRK1001",
                25.2048,
                55.2708,
                650,
                "HOSTILE",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 45, 25, 123_000_000)
        ));

        // T12 - Valid Track Number
        tracks.add(new Track(
                12,
                "TRK1001",
                25.2048,
                55.2708,
                650,
                "FRIEND",
                "INACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 50, 25, 123_000_000)
        ));

        // T13 - Valid Track Number
        tracks.add(new Track(
                13,
                "TRK1001",
                25.2048,
                55.2708,
                650,
                "HOSTILE",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 55, 25, 123_000_000)
        ));

        // T14 - Valid Track Number
        tracks.add(new Track(
                14,
                "TRK1001",
                25.2048,
                55.2708,
                650,
                "FRIEND",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 59, 25, 123_000_000)
        ));

        // T15 - Valid Track Number
        tracks.add(new Track(
                15,
                "TRK1002",
                25.2063,
                55.2708,
                650,
                "HOSTILE",
                "INACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 41, 25, 123_000_000)
        ));

        // T16 - Valid Track Number
        tracks.add(new Track(
                16,
                "TRK1001",
                25.2063,
                55.2708,
                800,
                "FRIEND",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 45, 25, 123_000_000)
        ));

        // T17 - Valid Track Number
        tracks.add(new Track(
                17,
                "TRK1001",
                25.2063,
                55.2708,
                650,
                "NEUTRAL",
                "INACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 50, 25, 123_000_000)
        ));

        // T18 - Valid Track Number
        tracks.add(new Track(
                18,
                "TRK1001",
                25.2063,
                55.2708,
                750,
                "FRIEND",
                "ACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 55, 25, 123_000_000)
        ));

        // T19 - Valid Track Number
        tracks.add(new Track(
                19,
                "TRK1001",
                25.2063,
                55.2708,
                650,
                "UNKNOWN",
                "INACTIVE",
                LocalDateTime.of(2026, 10, 28, 7, 59, 25, 123_000_000)
        ));

        return tracks;
    }
}



