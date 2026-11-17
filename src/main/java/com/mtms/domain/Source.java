package com.mtms.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sources")
public class Source {
    @Id
    private String sourceId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstSeenTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastReceivedTime;

    private Integer latestSequence;
    private String sourceStatus;
    private Integer receivedCount;
    private Integer acceptedCount;
    private Integer rejectedCount;
    private Integer duplicateCount;
    private Integer processingFailureCount;

    public Source() {}

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public LocalDateTime getFirstSeenTime() { return firstSeenTime; }
    public void setFirstSeenTime(LocalDateTime firstSeenTime) { this.firstSeenTime = firstSeenTime; }

    public LocalDateTime getLastReceivedTime() { return lastReceivedTime; }
    public void setLastReceivedTime(LocalDateTime lastReceivedTime) { this.lastReceivedTime = lastReceivedTime; }

    public Integer getLatestSequence() { return latestSequence; }
    public void setLatestSequence(Integer latestSequence) { this.latestSequence = latestSequence; }

    public String getSourceStatus() { return sourceStatus; }
    public void setSourceStatus(String sourceStatus) { this.sourceStatus = sourceStatus; }

    public Integer getReceivedCount() { return receivedCount; }
    public void setReceivedCount(Integer receivedCount) { this.receivedCount = receivedCount; }

    public Integer getAcceptedCount() { return acceptedCount; }
    public void setAcceptedCount(Integer acceptedCount) { this.acceptedCount = acceptedCount; }

    public Integer getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(Integer rejectedCount) { this.rejectedCount = rejectedCount; }

    public Integer getDuplicateCount() { return duplicateCount; }
    public void setDuplicateCount(Integer duplicateCount) { this.duplicateCount = duplicateCount; }

    public Integer getProcessingFailureCount() { return processingFailureCount; }
    public void setProcessingFailureCount(Integer processingFailureCount) { this.processingFailureCount = processingFailureCount; }
}
