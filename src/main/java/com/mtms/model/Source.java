package com.mtms.model;

import java.time.LocalDateTime;

public class Source {
    String sourceId;
    String sourceStatus;
    LocalDateTime firstSeen;
    LocalDateTime lastSeen;
    int highestSequence;
    int receivedCount;
    int acceptedCount;
    int rejectedCount;
    int duplicateCount;
    int processingFailureCount;

    public Source(String sourceId, String sourceStatus, LocalDateTime firstSeen, LocalDateTime lastSeen, int highestSequence, int receivedCount, int acceptedCount, int rejectedCount, int duplicateCount, int processingFailureCount) {
        this.sourceId = sourceId;
        this.sourceStatus = sourceStatus;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.highestSequence = highestSequence;
        this.receivedCount = receivedCount;
        this.acceptedCount = acceptedCount;
        this.rejectedCount = rejectedCount;
        this.duplicateCount = duplicateCount;
        this.processingFailureCount = processingFailureCount;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceStatus() {
        return sourceStatus;
    }

    public LocalDateTime getFirstSeen() {
        return firstSeen;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public int getHighestSequence() {
        return highestSequence;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public int getProcessingFailureCount() {
        return processingFailureCount;
    }

    @Override
    public String toString() {
        return "Source{" +
                "sourceId='" + sourceId + '\'' +
                ", sourceStatus='" + sourceStatus + '\'' +
                ", firstSeen=" + firstSeen +
                ", lastSeen=" + lastSeen +
                ", highestSequence=" + highestSequence +
                ", receivedCount=" + receivedCount +
                ", acceptedCount=" + acceptedCount +
                ", rejectedCount=" + rejectedCount +
                ", duplicateCount=" + duplicateCount +
                ", processingFailureCount=" + processingFailureCount +
                '}';
    }
}
