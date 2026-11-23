package com.mtms.service;

import com.mtms.model.Alert;
import com.mtms.model.Source;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
//This service calculates source health and saves status transitions to PostgreSQL. The receiver calls recordReceived() immediately, but that method does not perform a database operation.
public class SourceHealthService {
    @Autowired
    private SourceRepository source_repo;
    @Autowired
    private AlertRepository alertRepo;

    private final Map<String, SourceState> states = new ConcurrentHashMap<>();
    private volatile boolean running;
    private Thread monitorThread;

    /*
     * Retrieves a Source entity from the database by its unique source ID.
     *
     * @param id The unique source identifier.
     * @return The matching Source entity.
     */
    public Source findSourceById(String id) {
        return source_repo.findBySourceId(id).get();
    }

    /*
     * Retrieves all Source entities stored in the database.
     *
     * @return A list of all Source entities.
     */
    public List<Source> getAllSources() {
        List<Source> listOfSources = new ArrayList<>();
        source_repo.findAll().forEach(listOfSources::add);
        return listOfSources;

    }

    /*
     * Builds a lookup map of each source ID to its highest recorded sequence number.
     *
     * @return A Map where keys are source IDs and values are their highest sequence numbers.
     */
    public Map<String, Long> getSourcesAndSequences(){
        List<Source> listOfSources = getAllSources();
        Map<String, Long> sourcesAndSequences = new HashMap<>();
        for(Source source : listOfSources)
            sourcesAndSequences.put(source.getSourceId(), (long) source.getHighestSequence());
        return sourcesAndSequences;
    }

    // Runtime info for each source.
    private static class SourceState {
        volatile LocalDateTime lastReceived;
        volatile String status = "NOT SEEN";
    }

    // Register sources in memory and PostgreSQL.
    // Call during startup, not inside the UDP receive loop.
    /*
     * Ensures a source exists in the database and initializes
     *      its in-memory health tracking state.
     *
     * @param sourceId The unique source identifier to register.
     * @return true once the source is registered in DB and memory.
     * @throws Exception if database access fails.
     */
    public Boolean registerSource(String sourceId) throws Exception {
        Source saved = source_repo.findBySourceId(sourceId).orElseGet(() -> {
            Source source = new Source();
            source.setSourceId(sourceId);
            return source_repo.save(source);
        });

        SourceState state = new SourceState();
        state.lastReceived = saved.getLastSeen();
        state.status = saved.getSourceStatus() == null ? "NOT SEEN" : saved.getSourceStatus();
        states.putIfAbsent(sourceId, state);
        return true;
    }

    // Called as soon as a valid source ID is received.
    // No database access here.
    /*
     * Updates a source's in-memory timestamp and marks it ACTIVE
     *      immediately upon packet arrival (no DB call).
     *
     * @param sourceId   The unique source identifier.
     * @param receivedAt The timestamp when the packet arrived.
     */
    public void recordReceived(String sourceId, LocalDateTime receivedAt) {
        SourceState state = states.computeIfAbsent(
                sourceId, id -> new SourceState()
        );
        state.lastReceived = receivedAt;
        state.status = "ACTIVE";
    }

    /*
     * Determines a source's health status based on elapsed seconds
     *      since its last received message
     * (<=15s: ACTIVE, <=60s: STALE, >60s: DISCONNECTED, null: NOT SEEN).
     *
     * @param state The in-memory SourceState to evaluate.
     * @return The calculated status string.
     */
    private String calculateStatus(SourceState state) {
        if (state.lastReceived == null) {
            return "NOT SEEN";
        }
        long seconds = Duration.between(
                state.lastReceived,
                LocalDateTime.now()
        ).getSeconds();
        if (seconds <= 15) {
            return "ACTIVE";
        }
        if (seconds <= 60) {
            return "STALE";
        }
        return "DISCONNECTED";
    }

    /*
     * Evaluates all tracked sources in memory, updates the database if a status changed,
     * and generates a MEDIUM or HIGH alert if a source becomes STALE or DISCONNECTED.
     */
    public void checkHealth() {
        for (Map.Entry<String, SourceState> entry : states.entrySet()) {
            String sourceId = entry.getKey();
            SourceState state = entry.getValue();
            String newStatus = calculateStatus(state);
            String previousStatus = state.status;

            if (newStatus.equals(previousStatus)) {
                continue;
            }
            // Do not overwrite a newer receive event.
            LocalDateTime observedLast = state.lastReceived;
            if (!newStatus.equals(calculateStatus(state)) || observedLast != state.lastReceived) {
                continue;
            }
            try {
                // Save the new status to the sources table.
                int updated = source_repo.updateStatus(sourceId, newStatus);
                if (updated > 0){
                    state.status = newStatus;
                    System.out.println(sourceId + ": " + previousStatus + " -> " + newStatus);
                    // Generate Alerts only for health deterioration.
                    if (newStatus.equals("STALE") || newStatus.equals("DISCONNECTED")) {
                        createHealthAlert(sourceId, newStatus);
                    }
                }
            } catch (Exception e) {
                System.err.println("Source health update failed: " + e.getMessage());
            }
        }
    }

    /*
     * Creates and saves an alert when a source degrades
     *      to STALE (MEDIUM) or DISCONNECTED (HIGH).
     *
     * @param sourceId The unique source identifier.
     * @param status   The degraded status string ("STALE" or "DISCONNECTED").
     */
    private void createHealthAlert(String sourceId, String status) {
        String severity = status.equals("STALE") ? "MEDIUM" : "HIGH";
        Alert alert = new Alert(
                sourceId,
                "Source " + sourceId + " became " + status,
                severity,
                LocalDateTime.now()
        );
        Alert result = alertRepo.save(alert);
        if (result == null) {
            System.err.println("Could not save health Alert for " + sourceId);
        }
    }

    /*
     * Starts a background thread that runs checkHealth() once every second.
     */
    public synchronized void startMonitoring() {
        if (running) {return;
        }
        running = true;
        monitorThread = new Thread(() -> {
            while (running) {
                checkHealth();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    if (!running) {
                        break;
                    }
                }
            }

        }, "Source-Health-Monitor");

        monitorThread.start();
    }

    /*
     * Stops the background health monitoring thread and waits for it to terminate.
     */
    public void stopMonitoring() {
        running = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
            try {
                monitorThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /*
     * Prints all persisted Source records and their current
     *      health metrics to standard output.
     */
    public void printSourceStatus(){
        System.out.println("\n===== SOURCE HEALTH =====");
        for (Source source : getAllSources()) {
            System.out.println(source);
        }
    }

    /*
     * Updates database counters and in-memory state for a source
     *      based on a message processing outcome.
     *
     * @param sourceId   The unique source identifier.
     * @param receivedAt The timestamp when the message was received.
     * @param outcome    Must be "ACCEPTED", "REJECTED", "DUPLICATE", or "FAILURE".
     * @param sequence   The sequence number of the processed message.
     * @throws IllegalArgumentException if outcome is not one of the four allowed values.
     */
    public void recordOutcome(
            String sourceId,
            LocalDateTime receivedAt,
            String outcome,
            int sequence

    ) {
        if (!List.of(
                "ACCEPTED", "REJECTED", "DUPLICATE", "FAILURE"
        ).contains(outcome)) {
            throw new IllegalArgumentException("Invalid outcome");
        }
        int accepted = outcome.equals("ACCEPTED") ? 1 : 0;
        int rejected = outcome.equals("REJECTED") ? 1 : 0;
        int duplicate = outcome.equals("DUPLICATE") ? 1 : 0;
        int failure = outcome.equals("FAILURE") ? 1 : 0;
        int highest = accepted == 1 ? sequence : 0;
        source_repo.recordOutcome(
                sourceId,
                receivedAt,
                highest,
                accepted,
                rejected,
                duplicate,
                failure

        );
        SourceState state = states.computeIfAbsent(
                sourceId, id -> new SourceState()
        );
        // Do not move the receive time backwards.
        if (state.lastReceived == null ||
                receivedAt.isAfter(state.lastReceived)) {
            state.lastReceived = receivedAt;
        }
        // Restore ACTIVE only while the source is recently receiving.
        if (calculateStatus(state).equals("ACTIVE")) {
            state.status = "ACTIVE";
            source_repo.markSourceActive(sourceId);
        }
    }

}