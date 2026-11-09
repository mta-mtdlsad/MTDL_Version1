package com.mtms.service;

import com.mtms.domain.Source;
import com.mtms.domain.Alert;
import com.mtms.domain.Track;
import com.mtms.repository.SourceRepository;
import com.mtms.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Service
public class SourceService {

    // Main method to process incoming tracking data
    public static void processSources(Track track) {
        if (track == null) {
            System.out.println("Track data is null");
            return;
        }

        // Extract source information from Track
        String sourceId = track.getSourceId();
        long sequenceNumber = track.getSequenceNumber();
        String trackNumber = track.getTrackNumber();

        // Process the received message
        boolean received = receiveMessage(sourceId, sequenceNumber);
        if (!received) {
            System.out.println("Unable to process message from: " + sourceId);
        }
        // Check the health of all registered sources
        checkSourceHealth(trackNumber);
    }

    // Register a new source
    public static void registerSource(String sourceId) {
        Source existingSource = SourceRepository.getSourceById(sourceId);
        if (existingSource == null) {
            Source source = new Source(sourceId, LocalDateTime.now(), null, 0, "NOT SEEN");
            SourceRepository.registerSource(source);
            System.out.println("New source registered: " + sourceId);
        } else {
            System.out.println("Source already registered: " + sourceId);
        }
    }

    // Handle a received message from a source
    public static boolean receiveMessage(String sourceId, long sequenceNumber) {
        LocalDateTime receivedTime = LocalDateTime.now();
        Source source = SourceRepository.getSourceById(sourceId);

        // Automatically register new sources
        if (source == null) {
            registerSource(sourceId);
            source = SourceRepository.getSourceById(sourceId);
            if (source == null) {
                System.out.println("Unable to register source");
                return false;
            }
        }

        // Validate sequence number before updating receive time
        if (sequenceNumber <= 0) {
            System.out.println("Invalid sequence number from: " + sourceId);
            return false;
        }

        // Update sequence number atomically
        boolean sequenceUpdated =
                SourceRepository.updateLatestSequenceIfValid(sourceId, sequenceNumber);

        if (!sequenceUpdated) {
            System.out.println("Duplicate or out-of-order message from: " + sourceId);
            return false;
        }

        // Update receive time after successful validation
        SourceRepository.updateLastReceivedTime(sourceId, receivedTime);
        System.out.println("Message received successfully from: " + sourceId);
        return true;
    }

    // Calculate source health status
    private static String calculateSourceStatus(Source source, LocalDateTime checkTime) {
        if (source.getLastReceivedAt() == null) {
            return "NOT SEEN";
        }

        Duration timeDifference = Duration.between(source.getLastReceivedAt(), checkTime);
        if (timeDifference.compareTo(Duration.ofSeconds(15)) <= 0) {
            return "ACTIVE";
        } else if (timeDifference.compareTo(Duration.ofSeconds(60)) <= 0) {
            return "STALE";
        } else {
            return "DISCONNECTED";
        }
    }

    // Check health of all registered sources
    public static void checkSourceHealth(String trackNumber) {
        List<Source> sources = SourceRepository.getAllSources();
        if (sources == null || sources.isEmpty()) {
            return;
        }
        LocalDateTime checkTime = LocalDateTime.now();
        for (Source source : sources) {
            String oldStatus = source.getSourceStatus();
            String newStatus = calculateSourceStatus(source, checkTime);
            if (!newStatus.equals(oldStatus)) {
                Alert alert = null;
                if (newStatus.equals("STALE")) {
                    alert = new Alert(trackNumber, "Source " + source.getSourceId() + " has become stale.", "MEDIUM", checkTime, source.getSourceId(), "Source becomes stale");
                } else if (newStatus.equals("DISCONNECTED")) {
                    alert = new Alert(trackNumber, "Source " + source.getSourceId() + " has become disconnected.", "HIGH", checkTime, source.getSourceId(), "Source becomes disconnected");
                }

                // Update status and create alert transactionally
                boolean updated = SourceRepository.updateSourceStatus(source.getSourceId(), oldStatus, newStatus, checkTime, alert);
                if (updated) {
                    source.setSourceStatus(newStatus);
                    System.out.println("Source " + source.getSourceId() + " changed status from " + oldStatus + " to " + newStatus);
                }
            }
        }
    }

    // Retrieve all alerts
    public static List<Source> getAllSources() {
        List <Source> listOfSources = SourceRepository.getAllSources();
        System.out.println("\nRetrieving all sources: ");
        if (listOfSources != null)
            for (Source source : listOfSources) {
                System.out.println(source);
            }
        else
            System.out.println("No available sources");
        return listOfSources;
    }
}