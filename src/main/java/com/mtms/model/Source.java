package com.mtms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Source {
    @Id
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

    public Source(){

    }

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

    public void setSourceStatus(String sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setFirstSeen(LocalDateTime firstSeen) {
        this.firstSeen = firstSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setHighestSequence(int highestSequence) {
        this.highestSequence = highestSequence;
    }

    public void setReceivedCount(int receivedCount) {
        this.receivedCount = receivedCount;
    }

    public void setAcceptedCount(int acceptedCount) {
        this.acceptedCount = acceptedCount;
    }

    public void setRejectedCount(int rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public void setDuplicateCount(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public void setProcessingFailureCount(int processingFailureCount) {
        this.processingFailureCount = processingFailureCount;
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
