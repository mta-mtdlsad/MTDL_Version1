package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.util.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageProcessorService {

    public enum Outcome {ACCEPTED, REJECTED, DUPLICATE, FAILURE}
    public record ProcessingResult(Outcome outcome, boolean outOfOrder) {}

    private final TrackService trackService;
    private final AlertService alertService;
    private final SourceHealthService sourceHealthService;
    private final ValidationService validationService;
    // Counters.
    private int duplicateCount = 0;
    private int outOfOrderCount = 0;

    /*
     * Constructs MessageProcessorService with its required service dependencies.
     *
     * @param trackService        Service for persisting and querying Track records.
     * @param alertService        Service for creating and saving Alerts.
     * @param sourceHealthService Service for tracking source health and message counters.
     * @param validationService   Service for validating Track fields and geofences.
     */
    @Autowired
    public MessageProcessorService(
            TrackService trackService,
            AlertService alertService,
            SourceHealthService sourceHealthService,
            ValidationService validationService) {
        this.trackService = trackService;
        this.alertService = alertService;
        this.sourceHealthService = sourceHealthService;
        this.validationService = validationService;
    }

    /*
     * Validates, checks sequence order, deduplicates, and saves a
     *      parsed Track, updating source health metrics.
     *
     * @param track The parsed Track object to process.
     * @return A ProcessingResult record containing the Outcome
     *      (ACCEPTED, REJECTED, DUPLICATE, FAILURE) and whether it arrived out-of-order.
     */
    public ProcessingResult processDetailed(Track track) {
        ProcessingResult processingResult;
        // FAILURE if track is null
        if (track == null) {
            processingResult = new ProcessingResult(Outcome.FAILURE, false);
            return processingResult;
        }
        try {
            // STEP 1: Validate the track first
            List<ValidationResult> errors = validationService.validateTrack(track);
            if (!errors.isEmpty()) {
                // Create an alert for each validation error
                for (ValidationResult error : errors) {
                    try {
                        alertService.createAlert(track, error);
                        System.out.println(error);
                    } catch (Exception e) {
                        System.err.println("Validation alert failed: " + e.getMessage());
                    }
                }
                System.out.println("Track rejected: " + errors);
                processingResult = new ProcessingResult(Outcome.REJECTED, false);
                updateSource(track, processingResult);
                return processingResult;
            }

            // STEP 2: Check if track is out-of-order
            String sourceId = track.getSourceId();
            int sequence = track.getSequenceNumber();
            // Find highest sequence
            int highest = trackService.getHighestSequence(sourceId);
            // Check out-of-order
            boolean outOfOrder = sequence < highest;

            // STEP 3: Save valid track using TrackService
            boolean saved = trackService.saveTrack(track);
            if (!saved) {
                duplicateCount++;
                createAlert(track, "Duplicate message: " + sourceId + " sequence " + sequence);
                processingResult = new ProcessingResult(Outcome.DUPLICATE, false);
                updateSource(track, processingResult);
                return processingResult;
            }

            // STEP 4: Create an alert if track is out-of-order
            if (outOfOrder) {
                outOfOrderCount++;
                createAlert(track, "Out-of-order message: " + sourceId + " sequence " + sequence);
            }

            // STEP 5: Update source and return processingResult
            System.out.println("Saved Track: " + track);
            processingResult = new ProcessingResult(Outcome.ACCEPTED, outOfOrder);
            updateSource(track, processingResult);
            return processingResult;

        } catch (Exception e) {
            System.err.println("Track processing failed: " + e.getMessage());
            ProcessingResult result = new ProcessingResult(Outcome.FAILURE, false);
            try {
                updateSource(track, result);
            } catch (Exception updateError) {
                System.err.println(
                        "Failed to record processing failure: "
                                + updateError.getMessage()
                );
            }
            return result;
        }
    }

    /*
     * Helper method to create and save a LOW-severity alert
     *      for duplicate or out-of-order tracks.
     *
     * @param track   The Track that triggered the warning.
     * @param message The alert description message.
     */
    private void createAlert(Track track, String message) {
        ValidationResult result = new ValidationResult(message, "LOW");
        try {
            alertService.createAlert(track, result);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Processing alert failed: " + e.getMessage());
        }
    }

    /*
     * Records the processing outcome and sequence number in
     *      SourceHealthService for the track's source.
     *
     * @param track  The Track that was processed.
     * @param result The ProcessingResult containing the outcome status.
     */
    public void updateSource(Track track, ProcessingResult result) {
        sourceHealthService.recordOutcome(
                track.getSourceId(),
                LocalDateTime.now(),
                result.outcome().name(),
                track.getSequenceNumber()
        );
    }
}
