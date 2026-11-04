package com.service;
import com.domain.Track;
import com.domain.ValidationResult;

public class MessageProcessorService {
    public enum Outcome {ACCEPTED, DUPLICATE, FAILURE}
    public record ProcessingResult(Outcome outcome, boolean outOfOrder) {}

    // Repository handles all database operations.
    private final TrackService trackService = new TrackService();
    // Service handles alerts.
    private final AlertService alertService = new AlertService();
    // Counters.
    private int duplicateCount = 0;
    private int outOfOrderCount = 0;

    // Process a validated Track.
    public ProcessingResult processDetailed(Track track) {
        if (track == null) {
            return new ProcessingResult(Outcome.FAILURE, false);
        }

        String sourceId = track.getSource_id();
        int sequence = track.getSequence_number();
        try {
            // Find the highest sequence saved for this source.
            int highest = trackService.getHighestSequence(sourceId);
            // Check if the message arrived out of order.
            boolean outOfOrder = sequence < highest;

            // Save the Track using the repository.
            // Returns false when the source and sequence already exist.
            boolean saved = trackService.saveTrack(track);
            if (!saved) {
                duplicateCount++;
                createAlert(track, "Duplicate message: " + sourceId + " sequence " + sequence);
                return new ProcessingResult(Outcome.DUPLICATE, false);
            }
            // Track was saved successfully.
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