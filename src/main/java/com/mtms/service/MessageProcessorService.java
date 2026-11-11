package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.model.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageProcessorService {

    public enum Outcome {ACCEPTED, REJECTED, DUPLICATE, FAILURE}
    public record ProcessingResult(Outcome outcome, boolean outOfOrder) {}

    @Autowired
    private TrackService trackService;
    @Autowired
    private AlertService alertService;
    // Counters.
    private int duplicateCount = 0;
    private int outOfOrderCount = 0;

    // Process a validated Track.
    public ProcessingResult processDetailed(Track track) {
        if (track == null) {
            return new ProcessingResult(Outcome.FAILURE, false);
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
                return new ProcessingResult(Outcome.REJECTED, false);
            }
            // STEP 2: Track passed validation
            String sourceId = track.getSourceId();
            int sequence = track.getSequenceNumber();
            // Find highest sequence
            int highest = trackService.getHighestSequence(sourceId);
            // Check out-of-order
            boolean outOfOrder = sequence < highest;
            // STEP 3: Save valid track
            boolean saved = trackService.saveTrack(track);
            if (!saved) {
                duplicateCount++;
                createAlert(track, "Duplicate message: " + sourceId + " sequence " + sequence);
                return new ProcessingResult(Outcome.DUPLICATE, false);
            }
            // STEP 4: Check out-of-order
            if (outOfOrder) {
                outOfOrderCount++;
                createAlert(track, "Out-of-order message: " + sourceId + " sequence " + sequence);
            }
            System.out.println("Saved Track: " + track);
            return new ProcessingResult(Outcome.ACCEPTED, outOfOrder);
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
