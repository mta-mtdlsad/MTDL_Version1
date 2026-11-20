package com.mtms.simulator;

import com.mtms.model.GeoPath;
import com.mtms.model.LatLonPoint;
import com.mtms.service.SourceHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class TrackSimulator extends Simulator {
    @Autowired
    protected SourceHealthService source_health_service;

    // Used to generate timestamps in the required format.
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    protected List<String> scenarios;
    protected Map<String, Long> sourcesAndSequences;
    protected Map<String, List<GeoPath>> sourcesAndPaths;
    protected List<GeoPath> allPaths;
    protected String[] sources;

    public TrackSimulator() {
        scenarios = Arrays.asList("NORMAL", "HOSTILE", "MIXED");
    }

    @Override
    // Check the settings before creating the simulation
    protected void validateParameters(String scenario, int sourceNum, int tracksPerSource) {
        // params = {numberOfSources, numberOfTracks, totalObservations, sendingRate}
        if (running.get()) {
            throw new IllegalStateException("Simulation is already running");
        }
        else if (!scenarios.contains(scenario)) {
            throw new IllegalArgumentException("Invalid simulation scenario");
        }
        else if (sourceNum < 3 || sourceNum > 20) {
            throw new IllegalArgumentException("Sources must be between 3 and 20");
        }
        else if (tracksPerSource < 1 || tracksPerSource > 100) {
            throw new IllegalArgumentException("Tracks per source must be between 1 and 100");
        }
    }

    @Override
    // Retrieves sourceIds and their sequenceNumbers if any Sources already exist
    // Generates extra sourceIds + sequenceNumbers if needed
    protected void getSourcesAndSequences(int sourceNum) throws SQLException {
        sourcesAndSequences = source_health_service.getSourcesAndSequences();
        int current_sources = sourcesAndSequences.size();
        int extra_sources = sourceNum - current_sources;
        String sourceId;
        for(int i = current_sources; extra_sources > 0; extra_sources--) {
            sourceId = "LINK-" + (char) ('A' + i);
            sourcesAndSequences.put(sourceId, 0L);
            i++;
        }
        sources = sourcesAndSequences.keySet().toArray(String[]::new);
    }

    @Override
    protected void generatePaths() {
        allPaths = startupListener.getPaths();
    }

    @Override
    // Main loop for limited and continuous simulation
    protected void runSimulation(String scenario, int sourceNum, int tracksPerSource) {
        // Calculate how frequently to send messages.
        // 10 messages/sec = approximately 100 ms apart.
        // 4 messages/sec
        long intervalNanos = 1_000_000_000L / 4;
        long nextSendTime = System.nanoTime();
        int totalSent = 0;
        try {
            while(running.get()) {
                // Predefined correlation, out-of-order, and duplicates
                if(totalSent % 100 == 0) predefinedTracks();
                String message = simulateTrack(totalSent, scenario, sourceNum, tracksPerSource);
                sender.sendMessage(message); // Send the message through UDP.
                totalSent++;
                nextSendTime += intervalNanos; // Pace messages using a monotonic clock.

                long remaining = nextSendTime - System.nanoTime();
                if (remaining > 0) {
                    java.util.concurrent.TimeUnit.NANOSECONDS
                            .sleep(remaining);
                } else {
                    // If behind schedule, do not accumulate
                    // an unlimited backlog of send delays.
                    nextSendTime = System.nanoTime();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Simulation error: " + e.getMessage());
        } finally {
            running.set(false);
            System.out.println("Simulation finished. Messages sent: " + totalSent);
        }
    }

    // Simulates a single track based on randomly generated values
    // scenario, identity, and status are selected based on WeightedSelectors
    protected String simulateTrack(int totalSent, String scenario, int total_sources, int tracks_per_source) {
        String source = sources[totalSent % total_sources];
        long sequence = sourcesAndSequences.get(source) + 1;
        sourcesAndSequences.replace(source, sequence);

        int selected_track = (totalSent % (total_sources * tracks_per_source)) + 1;

        String trackNumber = String.format("TRK%05d", selected_track);

        String status;
        String identity;
        switch(scenario) {
            case "NORMAL": {
                status = "ACTIVE";
                if(selected_track % 5 % 3 == 0)
                    identity = "NEUTRAL";
                else
                    identity = "FRIEND";
                break;
            }
            case "MIXED": {
                if(random.nextInt(3) == 1)
                    status = "INACTIVE";
                else
                    status = "ACTIVE";
                if(selected_track % 5 % 3 == 0)
                    identity = "NEUTRAL";
                else
                    identity = "FRIEND";
                break;
            }
            case "HOSTILE": {
                status = "ACTIVE";
                if(selected_track % 5 % 3 == 0)
                    identity = "HOSTILE";
                else
                    identity = "FRIEND";
                break;
            }
            default: {
                status = "ACTIVE";
                identity = "UNKNOWN";
            }
        }

        GeoPath path = allPaths.get(selected_track-1);
        LatLonPoint point = path.getPoints().get(totalSent / (total_sources * tracks_per_source));
        String trackType = path.getPathType();
        double latitude = point.getLat();
        double longitude = point.getLon();
        double speed = 300.0 + random.nextInt(1500);

        if((sequence < 33) && (random.nextInt(5) == 1)){
            identity = "UNKNOWN";
            trackType = "UNKNOWN";
        }

        // Timestamp of the simulated observation.
        String observationTime = LocalDateTime.now().format(TIME_FORMAT);

        // Construct the required nine-field UDP message.
        return String.format(
                java.util.Locale.US,
                "%s|%d|%s|%s|%.6f|%.6f|%.2f|%s|%s|%s",
                source,
                sequence,
                trackNumber,
                trackType,
                latitude,
                longitude,
                speed,
                identity,
                status,
                observationTime
        );
    }

    protected void predefinedTracks() throws Exception {
        long sequenceLinkA = sourcesAndSequences.get("LINK-A") + 1;
        long sequenceLinkB = sourcesAndSequences.get("LINK-B") + 1;
        long sequenceLinkC = sourcesAndSequences.get("LINK-C") + 1;
        String timeNow = LocalDateTime.now().format(TIME_FORMAT);

        // First observation
        sender.sendMessage(
                "LINK-A|" + sequenceLinkA + "|TRK00101|AIR|24.971600|54.594600|450|FRIEND|ACTIVE|" + timeNow
        );
        // Higher sequence number
        sender.sendMessage(
                "LINK-A|" + (sequenceLinkA + 2) + "|TRK00101|AIR|24.971700|54.594700|455|FRIEND|ACTIVE|" + timeNow
        );
        // Duplicate: sequence 1 already exists
        sender.sendMessage(
                "LINK-A|" + sequenceLinkA + "|TRK00101|AIR|24.971700|54.594700|455|FRIEND|ACTIVE|" + timeNow
        );
        // Out of order: sequence 2 arrives after sequence 3
        sender.sendMessage(
                "LINK-A|" + (sequenceLinkA + 1) + "|TRK00101|AIR|24.971800|54.594800|460|FRIEND|ACTIVE|" + timeNow
        );
        // Different source: speed invalid
        sender.sendMessage(
                "LINK-B|" + sequenceLinkB + "|TRK00102|AIR|24.972000|54.595000|3000|FRIEND|ACTIVE|" + timeNow
        );

        // Correlation
        sender.sendMessage(
                "LINK-B|" + (sequenceLinkB + 1) + "|TRK00106|AIR|24.171600|54.194600|600|FRIEND|ACTIVE|" + timeNow
        );
        sender.sendMessage(
                "LINK-C|" + sequenceLinkC + "|TRK00107|AIR|24.171700|54.194700|605|FRIEND|ACTIVE|" + timeNow
        );

        // Correlation
        sender.sendMessage(
                "LINK-A|" + (sequenceLinkA + 3) + "|TRK00105|AIR|24.571800|54.594800|460|FRIEND|ACTIVE|" + timeNow
        );
        sender.sendMessage(
                "LINK-B|" + (sequenceLinkB + 2) + "|TRK00106|AIR|24.571600|54.594600|600|FRIEND|ACTIVE|" + timeNow
        );
        sender.sendMessage(
                "LINK-C|" + (sequenceLinkC + 1) + "|TRK00107|AIR|24.571700|54.594700|605|FRIEND|ACTIVE|" + timeNow
        );
        sourcesAndSequences.replace("LINK-A", sequenceLinkA + 3);
        sourcesAndSequences.replace("LINK-B", sequenceLinkB + 2);
        sourcesAndSequences.replace("LINK-C", sequenceLinkC + 1);
    }
}
