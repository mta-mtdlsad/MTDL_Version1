package com.mtms.simulator;

import com.mtms.domain.Path;
import com.mtms.domain.Track;
import com.mtms.service.PathService;
import com.mtms.service.TrackService;

import java.time.LocalDateTime;
import java.util.*;

public class TrackSimulator {
    private final Random random = new Random();
    private final TrackService trackService;
    private final PathService pathService;

    private final List<String> validIdentities = Arrays.asList("FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN");
    private final List<String> invalidIdentities = Arrays.asList("ENEMY", "TERRORIST", "ALIEN", "ALLY");
    private final List<String> validStatuses = Arrays.asList("ACTIVE", "INACTIVE");

    public TrackSimulator(TrackService trackService, PathService pathService) {
        this.trackService = trackService;
        this.pathService = pathService;
    }

    public List<Track> generateTracks() {
        List<Track> simulatedFeed = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        String[] sources = {"SRC_01", "SRC_02", "SRC_03", "SRC_04", "SRC_05", "SRC_06", "SRC_07", "SRC_08", "SRC_09", "SRC_10"};
        Map<String, Integer> latestSequences = trackService.getLatestSequenceMap();

        int[] sourceSequences = new int[sources.length];
        for (int i = 0; i < sources.length; i++) {
            sourceSequences[i] = latestSequences.getOrDefault(sources[i], 0);
        }

        List<Path> availablePaths = pathService.getAllPaths();

        for (int i = 1; i <= 500; i++) {
            int sourceIndex = (i - 1) % 10;
            String currentSource = sources[sourceIndex];

            sourceSequences[sourceIndex]++;
            int currentSequence = sourceSequences[sourceIndex];

            String trackNumber = "TRK" + (10000 + i);
            double latitude;
            double longitude;
            double speed = 100.0 + (random.nextDouble() * 800.0);
            String identity = validIdentities.get(random.nextInt(validIdentities.size()));
            String status = validStatuses.get(random.nextInt(validStatuses.size()));

            if (!availablePaths.isEmpty() && i % 3 == 0) {
                Path targetPath = availablePaths.get(random.nextInt(availablePaths.size()));
                double progress = random.nextDouble();
                latitude = targetPath.getStartLatitude() + (targetPath.getEndLatitude() - targetPath.getStartLatitude()) * progress;
                longitude = targetPath.getStartLongitude() + (targetPath.getEndLongitude() - targetPath.getStartLongitude()) * progress;
            } else {
                latitude = 20.0 + (random.nextDouble() * 20.0);
                longitude = 50.0 + (random.nextDouble() * 20.0);
            }


            if (i % 25 == 1) {
                trackNumber = (i % 2 == 0) ? "   " : null;
            } else if (i % 25 == 2) {
                trackNumber = "TRK";
            } else if (i % 25 == 3) {
                latitude = (i % 2 == 0) ? 95.5000 : -102.3500;
            } else if (i % 25 == 4) {
                longitude = (i % 2 == 0) ? 195.1200 : -220.6500;
            } else if (i % 25 == 5) {
                speed = (i % 2 == 0) ? 0.00 : -45.50;
            } else if (i % 25 == 6) {
                speed = 2500.00 + random.nextInt(1000);
            } else if (i % 25 == 7) {
                identity = invalidIdentities.get(random.nextInt(invalidIdentities.size()));
            } else if (i % 25 == 8) {
                status = (i % 2 == 0) ? "RUNNING" : "PAUSED";
            }

            //Valid Tracks
            Track track = new Track();
            track.setSourceId(currentSource);
            track.setSequenceNumber(currentSequence);
            track.setTrackNumber(trackNumber);
            track.setLatitude(latitude);
            track.setLongitude(longitude);
            track.setSpeed(speed);
            track.setIdentityType(identity);
            track.setTrackStatus(status);
            track.setCreatedTime(now);
            simulatedFeed.add(track);


            //Invalid Tracks
            if (i % 3 == 0 || i % 5 == 0) {
                Track invTrack = new Track();
                invTrack.setSourceId("SRC_TEST");
                invTrack.setSequenceNumber(currentSequence);
                invTrack.setTrackNumber("TRK");
                invTrack.setLatitude(1234);
                invTrack.setLongitude(1234);
                invTrack.setSpeed(1234);
                invTrack.setIdentityType("No Identity");
                invTrack.setTrackStatus("Unknown Status");
                invTrack.setCreatedTime(now);
                simulatedFeed.add(invTrack);
            }

            //Out of Order
            if (i % 5 == 0) {
                Track oldTrack = new Track();
                oldTrack.setSourceId(currentSource);
                oldTrack.setSequenceNumber(currentSequence - 1);
                oldTrack.setTrackNumber(trackNumber);
                oldTrack.setLatitude(latitude);
                oldTrack.setLongitude(longitude);
                oldTrack.setSpeed(speed);
                oldTrack.setIdentityType(identity);
                oldTrack.setTrackStatus(status);
                oldTrack.setCreatedTime(now.minusHours(1));
                simulatedFeed.add(oldTrack);
            }

            // Emit varying numbers of correlated observations (2, 3, or 4 total sources per group)
            if (i % 10 == 0) {
                int extraSources = 0;
                if (i % 30 == 0) {
                    extraSources = 3; // 1 original + 3 extra = 4 sources
                } else if (i % 20 == 0) {
                    extraSources = 2; // 1 original + 2 extra = 3 sources
                } else {
                    extraSources = 1; // 1 original + 1 extra = 2 sources
                }

                for (int offset = 1; offset <= extraSources; offset++) {
                    int otherSourceIndex = (sourceIndex + offset) % 10;
                    String otherSource = sources[otherSourceIndex];
                    sourceSequences[otherSourceIndex]++;

                    Track correlatedTrack = new Track();
                    correlatedTrack.setSourceId(otherSource);
                    correlatedTrack.setSequenceNumber(sourceSequences[otherSourceIndex]);
                    correlatedTrack.setTrackNumber("TRK" + (20000 + (i * 10) + offset));
                    correlatedTrack.setLatitude(latitude + (0.003 * offset));   // ~0.33 km per offset
                    correlatedTrack.setLongitude(longitude + (0.003 * offset));
                    correlatedTrack.setSpeed(speed);
                    correlatedTrack.setIdentityType(identity);
                    correlatedTrack.setTrackStatus(status);
                    correlatedTrack.setCreatedTime(now);
                    simulatedFeed.add(correlatedTrack);
                }
            }


        }

        return simulatedFeed;
    }
}
