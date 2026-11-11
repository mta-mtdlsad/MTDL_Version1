package com.mtms.service;

import com.mtms.model.Alert;
import com.mtms.model.Source;
import com.mtms.model.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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

    public Source findSourceById(String id) {
        return source_repo.findBySourceId(id).get();
    }

    public List<Source> getAllSources() {
        List<Source> listOfSources = new ArrayList<>();
        source_repo.findAll().forEach(listOfSources::add);
        return listOfSources;

    }

    // Returns SourceIds and their Sequence number
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
    public void recordReceived(String sourceId, LocalDateTime receivedAt) {
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
                int updated = source_repo.updateStatus(sourceId, newStatus);
                if (updated > 0){
                    state.status = newStatus;
                    System.out.println(sourceId + ": " + previousStatus + " -> " + newStatus);
                    // Generate Alerts only for health deterioration.
                    if (newStatus.equals("STALE") || newStatus.equals("DISCONNECTED")) {
//                        createHealthAlert(sourceId, newStatus);
                    }
                }
            } catch (Exception e) {
                System.err.println("Source health update failed: " + e.getMessage());
            }
        }
    }

//    // Save a health Alert in the existing alerts table.
//    private void createHealthAlert(
//            String sourceId, String status) {
//        String severity = status.equals("STALE") ? "MEDIUM" : "HIGH";
//        Alert alert = new Alert(
//                sourceId,
//                "Source " + sourceId + " became " + status,
//                severity,
//                LocalDateTime.now()
//        );
//        String result = alertRepo.saveAlert(alert);
//        if (result == null) {
//            System.err.println("Could not save health Alert for " + sourceId);
//        }
//    }

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
    public void printSourceStatus(){
        System.out.println("\n===== SOURCE HEALTH =====");
        for (Source source : getAllSources()) {
            System.out.println(source);
        }
    }

    public void recordOutcome(String sourceId, LocalDateTime receivedAt, String outcome, int sequence) {
        if (!List.of("ACCEPTED", "REJECTED", "DUPLICATE", "FAILURE"
        ).contains(outcome)) {
            throw new IllegalArgumentException("Invalid outcome");
        }
        int accepted = outcome.equals("ACCEPTED") ? 1 : 0;
        int rejected = outcome.equals("REJECTED") ? 1 : 0;
        int duplicate = outcome.equals("DUPLICATE") ? 1 : 0;
        int failure = (outcome.equals("REJECTED") || outcome.equals("DUPLICATE")) ? 1 : 0;
        int highest = accepted == 1 ? sequence : 0;
        source_repo.recordOutcome(sourceId, receivedAt, highest, accepted, rejected, duplicate, failure);
    }
}
