package com.network;

import com.repository.UDPObservationRepository;

import java.sql.SQLException;
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
    private final UDPObservationRepository repo = new UDPObservationRepository();

    // Counters
    private int duplicateCount = 0;
    private int outOfOrderCount = 0;

    public void process(UDPMessage message) {
        String sourceId = message.sourceId();
        Integer sequence = message.sequenceNumber();
        // Create a sequence set for a new source
        receivedSequences.putIfAbsent(sourceId, new HashSet<>());
        Set<Integer> sequences = receivedSequences.get(sourceId);
        // Check if this sequence was already received
        if (sequences.contains(sequence)) {
            duplicateCount++;
            System.out.println("Duplicate message: " + sourceId + " sequence " + sequence);
            return;
        }
        // Check if the message arrived out of order
        Integer highest = highestSequences.getOrDefault(
                sourceId, -1
        );
        boolean outOfOrder = sequence < highest;
        if (outOfOrder) {
            outOfOrderCount++;
            System.out.println("Out-of-order message: " + sourceId + " sequence " + sequence);
        }
        try {
            // Save the observation to PostgreSQL
            boolean saved = repo.saveObservation(message);
            if (!saved) {
                duplicateCount++;
                System.out.println("Database duplicate: " + sourceId + " sequence " + sequence);
                return;
            }
            // Remember the saved sequence number
            sequences.add(sequence);
            // Update the highest sequence number
            if (sequence > highest) {
                highestSequences.put(sourceId, sequence);
            }
            System.out.println("Saved observation: " + message);
        } catch (Exception e) {
            // Do not mark the message as accepted if saving fails
            System.out.println("Database error: " + e.getMessage());
        }
    }
    public int getDuplicateCount() {
        return duplicateCount;
    }
    public int getOutOfOrderCount() {
        return outOfOrderCount;
    }
}
