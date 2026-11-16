package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service facilitating bulk validation and batch insertion processing.
 * Registered as a Spring Service component.
 */
@Service
public class BatchTrackService {
    private final TrackRepository trackRepository;
    private final ValidationService validationService;
    private final AlertService alertService;

    // Constructs batch processing service with dependencies via Spring Autowired[cite: 8]
    @Autowired
    public BatchTrackService(TrackRepository trackRepository, ValidationService validationService, AlertService alertService) {
        this.trackRepository = trackRepository;
        this.validationService = validationService;
        this.alertService = alertService;
    }

    // Validates collection of tracks, logging invalid records and batch-inserting clean tracks[cite: 8]
    public void processTrackBatch(List<Track> incomingTracks) {
        List<Track> validTracksBatch = new ArrayList<>();
        int acceptedCount = 0;
        int rejectedCount = 0;

        System.out.printf("%n--- Analyzing Batch Records (%d records) ---%n", incomingTracks.size());

        for (Track track : incomingTracks) {
            List<String> errors = validationService.getValidationErrors(track);

            if (errors.isEmpty()) {
                validTracksBatch.add(track);
                acceptedCount++;
                System.out.printf("(%d) Track(s) is ready to be inserted; no Alert.%n", acceptedCount);
            } else {
                rejectedCount++;
                for (String error : errors) {
                    alertService.createAlert(track.getTrackNumber(), track.getSourceId(), error, "Track validation failure");
                }
            }
        }

        if (!validTracksBatch.isEmpty()) {
            System.out.printf("Committing %d valid tracks to database via transactional batch...%n", validTracksBatch.size());
            trackRepository.saveAll(validTracksBatch);
        }

        System.out.println("\n=== Batch Processing Execution Summary ===");
        System.out.println("Total Analyzed: " + incomingTracks.size());
        System.out.println("Valid Staged:   " + acceptedCount);
        System.out.println("Errors Logged:  " + rejectedCount);
        System.out.println("==========================================");
    }
}