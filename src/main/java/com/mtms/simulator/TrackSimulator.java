package com.mtms.simulator;

import com.mtms.model.WeightedSelector;
import com.mtms.service.SourceHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class TrackSimulator extends Simulator{
    @Autowired
    protected SourceHealthService source_health_service;

    // Used to generate timestamps in the required format.
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    protected List<String> scenarios;
    protected Map<String, Long> sourcesAndSequences;
    protected WeightedSelector source_selector;
    protected WeightedSelector identity_selector;
    protected WeightedSelector status_selector;

    public TrackSimulator() {
        scenarios = Arrays.asList("NORMAL", "HOSTILE", "MIXED");
    }

    @Override
    // Sends predefined message, no random generation, for testing only
    protected void predefinedSimulation() throws Exception {
        // First observation
        sender.sendMessage(
                "LINK-A|250|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:15:30"
        );
        // Higher sequence number
        sender.sendMessage(
                "LINK-A|255|TRK1001|12.971700|77.594700|455|FRIEND|ACTIVE|2026-11-02T09:15:35"
        );
        // Duplicate: sequence 250 already exists
        sender.sendMessage(
                "LINK-A|250|TRK1001|12.971700|77.594700|455|FRIEND|ACTIVE|2026-11-02T09:15:35"
        );
        // Out of order: sequence 253 arrives after sequence 255
        sender.sendMessage(
                "LINK-A|253|TRK1001|12.971800|77.594800|460|FRIEND|ACTIVE|2026-11-02T09:15:40"
        );
        // Different source: sequence 255 is allowed
        sender.sendMessage(
                "LINK-B|255|TRK1001|12.972000|77.595000|450|FRIEND|ACTIVE|2026-11-02T09:15:45"
        );
        // Different source: speed invalid
        sender.sendMessage(
                "LINK-B|256|TRK1001|12.972000|77.595000|3000|FRIEND|ACTIVE|2026-11-02T09:15:45"
        );
        // Different source: sequence 5 is allowed
        sender.sendMessage(
                "LINK-B|257|TRK1001|12.972000|77.595000|500|HOSTILE|ACTIVE|2026-11-02T09:15:45"
        );
        sender.sendMessage(
                "LINK-B|258|TRK1001|12.972000|77.595000|500|HOSTILE|ACTIVE|2026-11-02T09:15:45|9"
        );

        // Correlation
        sender.sendMessage(
                "LINK-A|260|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:15:30"
        );
        sender.sendMessage(
                "LINK-B|265|TRK2001|12.971700|77.594700|455|FRIEND|ACTIVE|2026-11-02T09:15:35"
        );
    }

    @Override
    // Check the settings before creating the simulation
    protected void validateParameters(String scenario, int mode, int[] params) {
        // params = {numberOfSources, numberOfTracks, totalObservations, sendingRate}
        if (running.get()) {
            throw new IllegalStateException("Simulation is already running");
        }
        else if (!scenarios.contains(scenario)) {
            throw new IllegalArgumentException("Invalid simulation scenario");
        }
        else if (mode < 1 || mode > 2) {
            throw new IllegalArgumentException("Invalid simulation mode");
        }
        else if (params[0] < 3 || params[0] > 20) {
            throw new IllegalArgumentException("Sources must be between 3 and 20");
        }
        else if (params[1] < 1 || params[1] > 100) {
            throw new IllegalArgumentException("Tracks per source must be between 1 and 100");
        }
        else if (params[2] < 1) {
            throw new IllegalArgumentException("Observations must be greater than zero");
        }
        else if (params[3] < 1 || params[3] > 1000) {
            throw new IllegalArgumentException(
                    "Rate must be between 1 and 1000");
        }
    }

    @Override
    // Retrieves sourceIds and their sequenceNumbers if any Sources already exist
    // Generates extra sourceIds + sequenceNumbers if needed
    protected void getSourcesAndSequences(int[] params) throws SQLException {
        sourcesAndSequences = source_health_service.getSourcesAndSequences();
        int current_sources = sourcesAndSequences.size();
        int extra_sources = params[0] - current_sources;
        String sourceId;
        for(int i = current_sources; extra_sources > 0; extra_sources--) {
            sourceId = "LINK-" + (char) ('A' + i);
            sourcesAndSequences.put(sourceId, 0L);
            i++;
        }
    }

    @Override
    // Generates weighted selectors that are used to generate source, identity, and status
    // based on scenario + random probabilities
    protected void generateWeightedSelectors(String scenario, int[] params) {
        String[] sources = sourcesAndSequences.keySet().toArray(String[]::new);
        List<Double> sources_weights = generateWeights(sourcesAndSequences.size(), 1.0);

        String[] identities = {"FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN"};
        List<Double> identity_weights;

        String[] statuses = {"ACTIVE", "INACTIVE"};
        List<Double> status_weights;

        double total_identity;
        if(scenario.equals("NORMAL")) {
            total_identity = 1.0;
            status_weights = Arrays.asList(1.0, 0.0);
        }
        else if(scenario.equals("HOSTILE")) {
            total_identity = 0.3;
            status_weights = Arrays.asList(1.0, 0.0);
        }
        else {
            total_identity = 0.9;
            status_weights = generateWeights(2, 1.0);
        }
        identity_weights = generateWeights(3, total_identity);
        identity_weights.add(1, (1.0 - total_identity));

        source_selector = new WeightedSelector(sources, sources_weights);
        identity_selector = new WeightedSelector(identities, identity_weights);
        status_selector = new WeightedSelector(statuses, status_weights);
    }

    // Randomly assigns weights for n options based on a given total weight
    protected List<Double> generateWeights(int numOfOptions, double total) {
        if(total - 0.2 < 0.0) {
            throw new IllegalArgumentException("Invalid total given to generateWeights");
        }

        // Separate 1.0 into sections
        List<Double> boundaries = new ArrayList<>();
        boundaries.add(0.0);
        boundaries.add(total - 0.15); //last weight (UNKNOWN) always 0.15
        boundaries.add(total);
        double temp = 0.0;
        for(int i = 0; i < (numOfOptions - 2); i++) {
            while (boundaries.contains(temp)) {
                temp = Math.round(random.nextDouble(0.0, (total - 0.15)) * 100.0) / 100.0;
            }
            boundaries.add(temp);
        }
        Collections.sort(boundaries);

        // Section size is weight
        List<Double> listOfWeights = new ArrayList<>();
        for(int j = 0; j < numOfOptions; j++) {
            temp = boundaries.get(j+1) - boundaries.get(j);
            temp = Math.round(temp * 100.0) / 100.0;
            listOfWeights.add(temp);
        }
        return listOfWeights;
    }

    @Override
    // Main loop for limited and continuous simulation
    protected void runSimulation(int mode, int[] params) {
        // Calculate how frequently to send messages.
        // 10 messages/sec = approximately 100 ms apart.
        long intervalNanos = 1_000_000_000L / params[3];
        long nextSendTime = System.nanoTime();
        int totalSent = 0;
        try {
            while(running.get() && (mode == 2 || totalSent < params[2])) {
                String message = simulateTrack(params[1]);

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
    protected String simulateTrack(int total_source_tracks) {
        String source = source_selector.getValue();
        long sequence = sourcesAndSequences.get(source) + 1;
        sourcesAndSequences.replace(source, sequence);

        int selected_track = random.nextInt(total_source_tracks);

        String trackNumber = String.format("TRK%d%03d", (source.charAt(5) - '@'), selected_track);

        String identity = identity_selector.getValue();
        String status = status_selector.getValue();

        double latitude = 10.0 + random.nextDouble();
        double longitude = 70.0 + random.nextDouble();
        double speed = 300.0 + random.nextInt(1500);

        // Timestamp of the simulated observation.
        String observationTime = LocalDateTime.now().format(TIME_FORMAT);

        // Construct the required nine-field UDP message.
        return String.format(
                java.util.Locale.US,
                "%s|%d|%s|%.6f|%.6f|%.2f|%s|%s|%s",
                source,
                sequence,
                trackNumber,
                latitude,
                longitude,
                speed,
                identity,
                status,
                observationTime
        );}
}
