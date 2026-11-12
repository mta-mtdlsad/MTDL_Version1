package com.mtms.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
//@Table(name = "source")
public class Source {

    //Attributes
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String sourceId;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastReceivedAt;
    private Integer latestSequence;
    private String sourceStatus;


    //Constructor
    public Source(String sourceId, LocalDateTime firstSeenAt, LocalDateTime lastReceivedAt, Integer latestSequence, String sourceStatus) {
        this.sourceId = sourceId;
        this.firstSeenAt = firstSeenAt;
        this.lastReceivedAt = lastReceivedAt;
        this.latestSequence = latestSequence;
        this.sourceStatus = sourceStatus;
    }

    //empty constructor

    public Source() {}

    //getter
    public String getSourceId() {return sourceId;}
    public LocalDateTime getFirstSeenAt() {return firstSeenAt;}
    public LocalDateTime getLastReceivedAt() {return lastReceivedAt;}
    public Integer getLatestSequence() {return latestSequence;}
    public String getSourceStatus() {return sourceStatus;}

    //setter
    public void setSourceStatus(String sourceStatus) {
        this.sourceStatus = sourceStatus;
    }
    public void setLastReceivedAt(LocalDateTime lastReceivedAt) {this.lastReceivedAt = lastReceivedAt;}
    public void setLatestSequence(Integer latestSequence) {this.latestSequence = latestSequence;}
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