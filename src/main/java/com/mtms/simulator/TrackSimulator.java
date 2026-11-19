package com.mtms.simulator;

import com.mtms.domain.Path;
import com.mtms.domain.Track;
import com.mtms.domain.TrackType;
import com.mtms.service.PathService;
import com.mtms.service.TrackService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Generates stable synthetic track histories on corridors assigned to each platform type. */
public class TrackSimulator {
    private static final int TRACK_COUNT = 10;
    private static final int OBSERVATIONS_PER_TRACK = 50;

    private final Random random = new Random();
    private final TrackService trackService;
    private final SimulationRouteCatalog routeCatalog = new SimulationRouteCatalog();

    private final List<String> validIdentities = List.of("FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN");
    private final List<String> invalidIdentities = List.of("ENEMY", "TERRORIST", "ALIEN", "ALLY");
    private final List<String> validStatuses = List.of("ACTIVE", "INACTIVE");
    private final TrackType[] trackTypes = {
            TrackType.SHIP, TrackType.AIRCRAFT, TrackType.LAND_VEHICLE, TrackType.SHIP,
            TrackType.AIRCRAFT, TrackType.LAND_VEHICLE, TrackType.SHIP, TrackType.AIRCRAFT,
            TrackType.LAND_VEHICLE, TrackType.SHIP
    };

    public TrackSimulator(TrackService trackService, PathService pathService) {
        this.trackService = trackService;
    }

    public List<Track> generateTracks() {
        List<Track> feed = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now();
        String[] sources = {"SRC_01", "SRC_02", "SRC_03", "SRC_04", "SRC_05", "SRC_06", "SRC_07", "SRC_08", "SRC_09", "SRC_10"};
        Map<String, Integer> latestSequences = trackService.getLatestSequenceMap();
        int[] sequences = new int[TRACK_COUNT];
        String[] identities = new String[TRACK_COUNT];
        String[] statuses = new String[TRACK_COUNT];
        SimulationRouteCatalog.Route[] routes = new SimulationRouteCatalog.Route[TRACK_COUNT];

        for (int i = 0; i < TRACK_COUNT; i++) {
            sequences[i] = latestSequences.getOrDefault(sources[i], 0);
            identities[i] = validIdentities.get(random.nextInt(validIdentities.size()));
            statuses[i] = validStatuses.get(random.nextInt(validStatuses.size()));
            routes[i] = routeCatalog.routeFor(trackTypes[i], i);
        }

        // Interleave 10 persistent tracks. Each next observation advances its same route.
        for (int step = 0; step < OBSERVATIONS_PER_TRACK; step++) {
            for (int trackIndex = 0; trackIndex < TRACK_COUNT; trackIndex++) {
                TrackType type = trackTypes[trackIndex];
                SimulationRouteCatalog.Coordinate point = routeCatalog.positionAt(routes[trackIndex], step, OBSERVATIONS_PER_TRACK);
                String trackNumber = "TRK" + (10000 + trackIndex);
                int sequence = ++sequences[trackIndex];
                double speed = speedFor(type);
                String identity = identities[trackIndex];
                String status = statuses[trackIndex];
                LocalDateTime observationTime = baseTime.plusSeconds(step * 10L);

                // Keep the baseline's invalid-value demonstrations without moving a
                // normal platform track outside its assigned geographic corridor.
                int caseNumber = (step * TRACK_COUNT + trackIndex + 1) % 25;
                if (caseNumber == 5) speed = 0.0;
                else if (caseNumber == 6) speed = 2500.0 + random.nextInt(1000);
                else if (caseNumber == 7) identity = invalidIdentities.get(random.nextInt(invalidIdentities.size()));
                else if (caseNumber == 8) status = random.nextBoolean() ? "RUNNING" : "PAUSED";

                feed.add(createTrack(sources[trackIndex], sequence, trackNumber,
                        point.latitude(), point.longitude(), speed, identity, status, type, observationTime));

                // Keep explicit malformed traffic isolated from the valid typed histories.
                int observationNumber = step * TRACK_COUNT + trackIndex + 1;
                if (observationNumber % 3 == 0 || observationNumber % 5 == 0) {
                    feed.add(createTrack("SRC_TEST", sequence, "TRK", 1234, 1234, 1234,
                            "No Identity", "Unknown Status", TrackType.UNKNOWN, observationTime));
                }

                // Preserve out-of-order timestamp/sequence demonstrations.
                if (observationNumber % 5 == 0) {
                    feed.add(createTrack(sources[trackIndex], sequence - 1, trackNumber,
                            point.latitude(), point.longitude(), speed, identity, status, type, observationTime.minusHours(1)));
                }

                // Keep near-position correlated observations and preserve the platform class.
                if (observationNumber % 10 == 0) {
                    int extras = observationNumber % 30 == 0 ? 3 : observationNumber % 20 == 0 ? 2 : 1;
                    for (int offset = 1; offset <= extras; offset++) {
                        int otherIndex = (trackIndex + offset) % TRACK_COUNT;
                        int otherSequence = ++sequences[otherIndex];
                        feed.add(createTrack(sources[otherIndex], otherSequence,
                                "TRK" + (20000 + observationNumber * 10 + offset),
                                point.latitude() + 0.003 * offset, point.longitude() + 0.003 * offset,
                                speed, identity, status, type, observationTime));
                    }
                }
            }
        }
        return feed;
    }

    private double speedFor(TrackType type) {
        return switch (type) {
            case SHIP -> 8.0 + random.nextDouble() * 22.0;
            case AIRCRAFT -> 180.0 + random.nextDouble() * 320.0;
            case LAND_VEHICLE -> 15.0 + random.nextDouble() * 85.0;
            case UNKNOWN -> 50.0;
        };
    }

    private Track createTrack(String source, int sequence, String number, double latitude, double longitude,
                              double speed, String identity, String status, TrackType type, LocalDateTime time) {
        Track track = new Track();
        track.setSourceId(source);
        track.setSequenceNumber(sequence);
        track.setTrackNumber(number);
        track.setLatitude(latitude);
        track.setLongitude(longitude);
        track.setSpeed(speed);
        track.setIdentityType(identity);
        track.setTrackStatus(status);
        track.setPlatformType(type);
        track.setCreatedTime(time);
        return track;
    }
}
