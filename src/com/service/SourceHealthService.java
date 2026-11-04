package com.service;

import com.domain.Alert;
import com.domain.Source;
import com.repository.AlertRepository;
import com.repository.SourceRepository;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//This service calculates source health and saves status transitions to PostgreSQL. The receiver calls recordReceived() immediately, but that method does not perform a database operation.
public class SourceHealthService {
    private final SourceRepository sourceRepo = new SourceRepository();

    private final Map<String, SourceState> states = new ConcurrentHashMap<>();
    private volatile boolean running;
    private Thread monitorThread;

    // Runtime info for each source.
    private static class SourceState {
        volatile LocalDateTime lastReceived;
        volatile String status = "NOT SEEN";
    }

    // Register sources in memory and PostgreSQL.
    // Call during startup, not inside the UDP receive loop.
    public void registerSource(String sourceId) throws Exception {
        sourceRepo.registerSource(sourceId);
        Source saved = sourceRepo.getSourceById(sourceId);
        SourceState state = new SourceState();
        if (saved != null) {
            state.lastReceived = saved.getLast_seen();
            state.status = saved.getSource_status() == null ? "NOT SEEN" : saved.getSource_status();
        }

        states.putIfAbsent(sourceId, state);
    }

    // Called as soon as a valid source ID is received.
    // No database access here.
    public void recordReceived(
            String sourceId, LocalDateTime receivedAt) {
        SourceState state = states.computeIfAbsent(
                sourceId, id -> new SourceState()
        );
        state.lastReceived = receivedAt;
        state.status = "ACTIVE";
    }

    // Calculate status using receive time.
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

    // Check source health every second.
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
                sourceRepo.updateStatus(sourceId, newStatus);
                state.status = newStatus;
                System.out.println(sourceId + ": " + previousStatus + " -> " + newStatus
                );
                // Generate Alerts only for health deterioration.
                if (newStatus.equals("STALE") || newStatus.equals("DISCONNECTED")) {
                    createHealthAlert(sourceId, newStatus);
                }
            } catch (Exception e) {
                System.err.println("Source health update failed: " + e.getMessage()
                );
            }
        }
    }

    // Save a health Alert in the existing alerts table.
    private void createHealthAlert(
            String sourceId, String status) {
        String severity = status.equals("STALE") ? "MEDIUM" : "HIGH";

        Alert alert = new Alert(
                sourceId,
                "Source " + sourceId + " became " + status,
                severity,
                LocalDateTime.now()
        );

        String result = new AlertRepository().saveAlert(alert);

        if (result == null) {
            System.err.println(
                    "Could not save health Alert for " + sourceId
            );
        }
    }

    // Start background monitoring.
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

    // Stop the monitoring thread.
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

    // Display persisted source information.
    public void printSourceStatus() throws SQLException {

        System.out.println("\n===== SOURCE HEALTH =====");

        for (Source source : sourceRepo.getAllSources()) {
            System.out.println(source);
        }
    }

    public void recordOutcome(String sourceId, LocalDateTime receivedAt, String outcome, int sequence) throws SQLException {
        sourceRepo.recordOutcome(sourceId, receivedAt, outcome, sequence);
    }
}

