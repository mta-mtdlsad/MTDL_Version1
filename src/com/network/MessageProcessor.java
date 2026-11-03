package com.network;

import com.domain.Track;
import com.domain.ValidationResult;
import com.repository.TrackRepository;
import com.service.AlertService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageProcessor {
    // Stores received sequence numbers for each source
    private final Map<String, Set<Integer>> receivedSequences = new HashMap<>();
    // Stores the highest sequence number for each source
    private final Map<String, Integer> highestSequences = new HashMap<>();
    // object of the repository
    private final TrackRepository repo = new TrackRepository();
    // object for alert service to create required alerts
    private final AlertService alertService = new AlertService();

    // Counters
    private int duplicateCount = 0;
    private int outOfOrderCount = 0;

    public boolean process(Track track) {
        if(track == null) return false;
        String sourceId = track.getSource_id();
        Integer sequence = track.getSequence_number();
        // Create a sequence set for a new source
        receivedSequences.putIfAbsent(sourceId, new HashSet<>());
        Set<Integer> sequences = receivedSequences.get(sourceId);
        // Check if this sequence was already received
        if (sequences.contains(sequence)) {
            duplicateCount++;
            ValidationResult result = new ValidationResult("Duplicate message: " + sourceId + " sequence " + sequence, "LOW");
            System.out.println(result.toString());
            alertService.createAlert(track, result);
            return false;
        }
        // Check if the message arrived out of order
        Integer highest = highestSequences.getOrDefault(
                sourceId, -1
        );
        boolean outOfOrder = sequence < highest;
        if (outOfOrder) {
            outOfOrderCount++;
            ValidationResult result = new ValidationResult("Out-of-order message: " + sourceId + " sequence " + sequence, "LOW");
            System.out.println(result.toString());
            alertService.createAlert(track, result);
        }
        try {
            // Save the observation to PostgreSQL
            boolean saved = repo.saveTrack(track);
            if (!saved) {
                duplicateCount++;
                System.out.println("Database duplicate: " + sourceId + " sequence " + sequence);
                return false;
            }
            // Remember the saved sequence number
            sequences.add(sequence);
            // Update the highest sequence number
            if (sequence > highest) {
                highestSequences.put(sourceId, sequence);
            }
            System.out.println("Saved Track: " + track);
        } catch (Exception e) {
            // Do not mark the message as accepted if saving fails
            ValidationResult result = new ValidationResult("Processing failure: " + e.getMessage(), "HIGH");
            System.out.println(result.toString());
            alertService.createAlert(track, result);
            return false;
        }
        return true;
    }
    public int getDuplicateCount() {
        return duplicateCount;
    }
    public int getOutOfOrderCount() {
        return outOfOrderCount;
    }
}
