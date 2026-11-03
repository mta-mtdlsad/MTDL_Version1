package com.simulator;

import com.network.UDPSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrackSimulator {

    // Controls whether the simulation is running.
    // AtomicBoolean allows the Stop button to safely
    // communicate with the background simulation thread.
    private final AtomicBoolean running = new AtomicBoolean(false);

    // The background thread used for simulation.
    private Thread simulationThread;

    private final UDPSender sender = new UDPSender();

    // Used to generate timestamps in the required format.
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // These values belong to the currently configured run.
    private String[] sourceIds;
    private long[] sequenceIds;

    private int tracksPerSource;
    private int ratePerSecond;
    private String scenario;

    private Random random;

    // Start a limited simulation.
    // For demo we will use: 3 sources, 5 Tracks each, 100 messages, 10/sec.
    public synchronized void startLimited(int numberOfSources, int numberOfTracks, int totalObservations, int rate, String selectedScenario, long seed) {
        validateSettings(numberOfSources, numberOfTracks, rate, selectedScenario
        );

        if (totalObservations <= 0) {
            throw new IllegalArgumentException("Observations must be greater than zero");
        }

        prepareRun(numberOfSources,
                numberOfTracks,
                rate,
                selectedScenario,
                seed
        );

        // Start sending in the background.
        simulationThread = new Thread(
                () -> simulate(totalObservations, false),
                "multi-source-simulator"
        );

        simulationThread.start();
    }

    // Start a continuous simulation.
    // It keeps sending until stopSimulation() is called.
    public synchronized void startContinuous(
            int numberOfSources,
            int numberOfTracks,
            int rate,
            String selectedScenario,
            long seed) {

        validateSettings(
                numberOfSources,
                numberOfTracks,
                rate,
                selectedScenario
        );

        prepareRun(
                numberOfSources,
                numberOfTracks,
                rate,
                selectedScenario,
                seed
        );

        simulationThread = new Thread(
                () -> simulate(0, true),
                "multi-source-simulator"
        );

        simulationThread.start();
    }

    // Check the settings before creating the simulation.
    private void validateSettings(int sources, int tracks, int rate, String scenario) {
        if (running.get()) {
            throw new IllegalStateException(
                    "Simulation is already running");
        }
        if (sources < 3 || sources > 20) {
            throw new IllegalArgumentException("Sources must be between 3 and 20");
        }

        if (tracks < 1 || tracks > 100) {
            throw new IllegalArgumentException("Tracks per source must be between 1 and 100");
        }

        if (rate < 1 || rate > 1000) {
            throw new IllegalArgumentException(
                    "Rate must be between 1 and 1000");
        }
        // to procide at least 3 named scenario: normal taffic, incresed hostile activity, and mixed active/inactive
        if (!Set.of("NORMAL", "HOSTILE", "MIXED").contains(scenario)) {
            throw new IllegalArgumentException("Invalid simulation scenario");
        }
    }

    // Initialize the source IDs, random generator,
    // and configuration for the new simulation.
    private void prepareRun(int sources, int tracks, int rate, String selectedScenario, long seed) {

        this.sourceIds = new String[sources];
        this.sequenceIds = new long[sources];

        this.tracksPerSource = tracks;
        this.ratePerSecond = rate;
        this.scenario = selectedScenario;

        // Same seed gives the same random-number sequence.
        this.random = new Random(seed);

        // Create Link-A, Link-B, Link-C, etc.
        for (int i = 0; i < sources; i++) {
            sourceIds[i] = "LINK-" + (char) ('A' + i);

            // Starting at zero means the first message will use sequence number 1.
            sequenceIds[i] = 0;
        }

        running.set(true);
    }

    // Main loop for limited or continuous simulation.
    private void simulate(int totalObservations, boolean continuous) {

        int sent = 0;

        // Calculate how frequently to send messages.
        // 10 messages/sec = approximately 100 ms apart.
        long intervalNanos = 1_000_000_000L / ratePerSecond;

        long nextSendTime = System.nanoTime();

        try {

            while (running.get() && (continuous || sent < totalObservations)) {

                // Distribute messages across the sources.
                int sourceIndex = sent % sourceIds.length;

                // Generate a Track observation.
                String message = simulateTrack(
                        sourceIndex,
                        sent
                );

                // Send the message through UDP.
                sender.sendMessage(message);

                sent++;

                // Pace messages using a monotonic clock.
                nextSendTime += intervalNanos;

                long remaining =
                        nextSendTime - System.nanoTime();

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
            System.err.println(
                    "Simulation error: " + e.getMessage());

        } finally {
            running.set(false);

            System.out.println("Simulation finished. Messages sent: " + sent);
        }
    }

    // Generate one Track observation for a specific source.
    public String simulateTrack(
            int sourceIndex,
            int messageIndex) {

        // Each source has its own sequence counter.
        long sequence = ++sequenceIds[sourceIndex];

        // Rotate through the configured Tracks.
        // Repeated observations use the same Track number.
        int trackIndex =
                (messageIndex / sourceIds.length)
                        % tracksPerSource;

        // Link-A -> TRK1001...
        // Link-B -> TRK2001...
        // Link-C -> TRK3001...
        String trackNumber = String.format("TRK%d%03d", sourceIndex + 1, trackIndex + 1);

        // Generate simulated position and speed values.
        double latitude =
                10.0 + random.nextDouble();

        double longitude =
                70.0 + random.nextDouble();

        double speed =
                300.0 + random.nextInt(1500);

        // Select identity and status based on scenario.
        String identity = generateIdentity();
        String status = generateStatus();

        // Timestamp of the simulated observation.
        String observationTime =
                LocalDateTime.now().format(TIME_FORMAT);

        // Construct the required nine-field UDP message.
        return String.format(
                java.util.Locale.US,
                "%s|%d|%s|%.6f|%.6f|%.2f|%s|%s|%s",
                sourceIds[sourceIndex],
                sequence,
                trackNumber,
                latitude,
                longitude,
                speed,
                identity,
                status,
                observationTime
        );
    }

    // Select identity according to the scenario.
    private String generateIdentity() {

        if ("NORMAL".equals(scenario)) {
            return "FRIEND";
        }

        if ("HOSTILE".equals(scenario)) {
            // 70% chance of HOSTILE.
            return random.nextInt(100) < 70
                    ? "HOSTILE" : "FRIEND";
        }

        // MIXED scenario: all four identities possible.
        String[] identities = {
                "FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN"
        };

        return identities[random.nextInt(identities.length)];
    }

    // Select Track status according to the scenario.
    private String generateStatus() {

        if ("MIXED".equals(scenario)) {
            return random.nextBoolean()
                    ? "ACTIVE" : "INACTIVE";
        }

        return "ACTIVE";
    }

    // Stop the running simulation.
    public synchronized void stopSimulation() {

        running.set(false);

        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }

    // Used by the web Servlet to display simulation status.
    public boolean isRunning() {
        return running.get();
    }
}