package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import java.util.ArrayList;
import java.util.List;

public class BatchTrackService {
    private final TrackRepository trackRepository;
    private final ValidationService validationService;
    private final AlertService alertService;

    public BatchTrackService(TrackRepository trackRepository, ValidationService validationService, AlertService alertService) {
        this.trackRepository = trackRepository;
        this.validationService = validationService;
        this.alertService = alertService;
    }


     // Splits incoming data packets into valid operational targets and logged failures.
    public void processTrackBatch(List<Track> incomingTracks) {
        List<Track> validTracksBatch = new ArrayList<>();
        int acceptedCount = 0;
        int rejectedCount = 0;

        System.out.printf("\n--- Analyzing Batch Records (%d records) ---%n", incomingTracks.size());

        for (Track track : incomingTracks) {
            List<String> errors = validationService.getValidationErrors(track);

            if (errors.isEmpty()) {
                // If clean, stage it for the atomic transaction batch insertion
                validTracksBatch.add(track);
                acceptedCount++;
                System.out.printf("(%d) Track(s) is ready to be inserted; no Alert.\n", acceptedCount);
            } else {
                // If invalid, trigger alert logging immediately (outside the track transaction)
                rejectedCount++;
                for (String error : errors) {
                    alertService.createAlert(track.track_number, error);
                }
            }
        }

        // Commit all valid tracks to the database in a single network transaction
        if (!validTracksBatch.isEmpty()) {
            System.out.printf("Commiting %d valid tracks to database via transactional batch...%n", validTracksBatch.size());
            trackRepository.insertBatch(validTracksBatch);
        }

        System.out.println("\n=== Batch Processing Execution Summary ===");
        System.out.println("Total Analyzed: " + incomingTracks.size());
        System.out.println("Valid Staged:   " + acceptedCount);
        System.out.println("Errors Logged:  " + rejectedCount);
        System.out.println("==========================================");
    }
}
