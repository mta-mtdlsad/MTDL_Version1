package com.mtms.domain;

import java.time.LocalDateTime;

public class Source {
    //Attributes
    private String sourceId;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastReceivedAt;
    private long latestSequence;
    private String sourceStatus;

    //Constructor
    public Source(String sourceId, LocalDateTime firstSeenAt, LocalDateTime lastReceivedAt, long latestSequence, String sourceStatus) {
        this.sourceId = sourceId;
        this.firstSeenAt = firstSeenAt;
        this.lastReceivedAt = lastReceivedAt;
        this.latestSequence = latestSequence;
        this.sourceStatus = sourceStatus;
    }

    //getter
    public String getSourceId() {return sourceId;}
    public LocalDateTime getFirstSeenAt() {return firstSeenAt;}
    public LocalDateTime getLastReceivedAt() {return lastReceivedAt;}
    public long getLatestSequence() {return latestSequence;}
    public String getSourceStatus() {return sourceStatus;}

    //setter
    public void setSourceStatus(String sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    //toString
    @Override
    public String toString() {
        return "Source{" +
                "sourceId='" + sourceId + '\'' +
                ", firstSeenAt=" + firstSeenAt +
                ", lastReceivedAt=" + lastReceivedAt +
                ", latestSequence=" + latestSequence +
                ", sourceStatus='" + sourceStatus + '\'' +
                '}';
    }
}
