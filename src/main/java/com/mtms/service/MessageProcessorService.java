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

    @Autowired
    private TrackService trackService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private SourceHealthService sourceHealthService;
    // Counters.
    private int duplicateCount = 0;
    private int outOfOrderCount = 0;

    // Process a validated Track.
    public ProcessingResult processDetailed(Track track) {
        ProcessingResult processingResult;
        // FAILURE if track is null
        if (track == null) {
            processingResult = new ProcessingResult(Outcome.FAILURE, false);
            //updateSource(track, processingResult);
            return processingResult;
        }
        try {
            // STEP 1: Validate the track first
            List<ValidationResult> errors = ValidationService.validateTrack(track);
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
            return new ProcessingResult(Outcome.FAILURE, false);
        }
    }

    // Create duplicate and out-of-order alerts.
    private void createAlert(Track track, String message) {
        ValidationResult result = new ValidationResult(message, "LOW");
        try {
            alertService.createAlert(track, result);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Processing alert failed: " + e.getMessage());
        }
    }

    // Updates source counters using SourceHealthService
    public void updateSource(Track track, ProcessingResult result) {
        sourceHealthService.recordOutcome(
                track.getSourceId(),
                LocalDateTime.now(),
                result.outcome().name(),
                track.getSequenceNumber()
        );
    }

    // Compatibility with existing code expecting boolean.
    public boolean process(Track track) {
        return processDetailed(track).outcome() == Outcome.ACCEPTED;
    }
    public int getDuplicateCount() {
        return duplicateCount;
    }
    public int getOutOfOrderCount() {
        return outOfOrderCount;
    }
}
