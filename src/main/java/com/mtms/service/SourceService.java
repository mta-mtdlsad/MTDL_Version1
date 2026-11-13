package com.mtms.service;
import com.mtms.domain.Alert;
import com.mtms.domain.Source;
//import com.mtms.repository.SourceRepository;
//import com.mtms.repository.AlertRepository;
import com.mtms.domain.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.SourceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SourceService {

    private SourceRepository sourceRepository;
    private AlertRepository alertRepository;

    @Autowired
    public SourceService(SourceRepository sourceRepository, AlertRepository alertRepository) {
        this.sourceRepository = sourceRepository;
        this.alertRepository = alertRepository;
    }

    public SourceService() {
    }

    //    private Source listOfSources;
    public static List<Source> listOfSources;


    // Main method to process incoming tracking data
    public void processSources(Track track) {
        if (track == null) {
            System.out.println("Track data is null");
            return;
        }

        // Extract source information from Track
        String sourceId = track.getSourceId();
        Integer sequenceNumber = track.getSequenceNumber();
        String trackNumber = track.getTrackNumber();

        // Process the received message
        boolean received = receiveMessage(sourceId, sequenceNumber);
        if (!received) {
            System.out.println("Unable to process message from: " + sourceId);
        }
        // Check the health of all registered sources
        checkSourceHealth(trackNumber);
    }

    // Handle a received message from a source
    @Transactional
    public boolean receiveMessage(String sourceId, Integer sequenceNumber) {
        LocalDateTime receivedTime = LocalDateTime.now();

        // Retrieve source
        Source source = sourceRepository.findBySourceId(sourceId);

        // Automatically register new sources
        if (source == null) {
            registerSource(sourceId);
            source = sourceRepository.findBySourceId(sourceId);
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

        // Check whether sequence number is valid
        if (sequenceNumber <= source.getLatestSequence()) {
            System.out.println(
                    "Duplicate or out-of-order message from: " + sourceId);
            return false;
        }

        // Update sequence number
        source.setLatestSequence(sequenceNumber);

        // Update last received time
        source.setLastReceivedAt(receivedTime);

        // Activate source
        source.setSourceStatus("ACTIVE");

        // Save updated source
        sourceRepository.save(source);

        System.out.println("Message received successfully from: " + sourceId);
        return true;
    }

    // Register a new source
    public void registerSource(String sourceId) {
        Source existingSource = sourceRepository.findBySourceId(sourceId);
        if (existingSource == null) {
            Source source = new Source(sourceId, LocalDateTime.now(), null, 0, "NOT SEEN");
            sourceRepository.save(source);
            System.out.println("New source registered: " + sourceId);
        } else {
            System.out.println("Source already registered: " + sourceId);
        }
    }


    // Calculate source health status
    public String calculateSourceStatus(Source source, LocalDateTime checkTime) {
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
    @Transactional
    public void checkSourceHealth(String trackNumber) {
        checkSourceHealth(trackNumber, LocalDateTime.now());
    }

    //overloaded version for testing purposes
    @Transactional
    public void checkSourceHealth(String trackNumber, LocalDateTime checkTime) {
        List<Source> sources = new ArrayList<>();
        sourceRepository.findAll().forEach(sources::add);

        if (sources.isEmpty()) {
            return;
        }

        for (Source source : sources) {
            String oldStatus = source.getSourceStatus();
            String newStatus = calculateSourceStatus(source, checkTime);

            if (!newStatus.equals(oldStatus)) {
                Alert alert = null;

                // Create alert if source becomes STALE
                if (newStatus.equals("STALE")) {
                    alert = new Alert(trackNumber, "Source " + source.getSourceId() + " has become stale.",
                            "MEDIUM", checkTime, source.getSourceId(), "Source becomes stale");
                }

                // Create alert if source becomes DISCONNECTED
                else if (newStatus.equals("DISCONNECTED")) {
                    alert = new Alert(trackNumber, "Source " + source.getSourceId() + " has become disconnected.",
                            "HIGH", checkTime, source.getSourceId(), "Source becomes disconnected");
                }

                // Update source status
                source.setSourceStatus(newStatus);

                // Save updated source
                sourceRepository.save(source);

                // Save alert if one was created
                if (alert != null) {
                    alertRepository.save(alert);
                }
                System.out.println("Source " + source.getSourceId() + " changed status from " + oldStatus + " to " + newStatus);
            }
        }
    }



    //findallsources
    public List<Source> findAllSources() {
        List<Source> listOfSources = new ArrayList<>();
        sourceRepository.findAll().forEach(listOfSources::add);
        return listOfSources;
    }

    //findsourcebyid
    public List<Source> findSourceById(String sourceId) {
        List<Source> listOfSources = new ArrayList<>();
        Source source = sourceRepository.findBySourceId(sourceId);

        System.out.println("\nRetrieving all Sources with Source Id " + sourceId + ":");
        if (source != null) {
            listOfSources.add(source);
            System.out.println(source);
        } else {
            System.out.println("No available Sources with source Id " + sourceId );
        }
        return listOfSources;
    }
}