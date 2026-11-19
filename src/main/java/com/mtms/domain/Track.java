package com.mtms.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracks")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sourceId", insertable = false, updatable = false)
    private Source source;

    @Column(name = "sourceId")
    private String sourceId;

    private Integer sequenceNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pathId", insertable = false, updatable = false)
    private Path path;

    @Column(name = "pathId")
    private Long pathId;

    private String trackNumber;
    private double latitude;
    private double longitude;
    private double speed;
    private String identityType;
    private String trackStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_type", length = 20)
    private TrackType platformType = TrackType.UNKNOWN;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;

    public Track() {}

    public Long getTrackId() { return trackId; }
    public void setTrackId(Long trackId) { this.trackId = trackId; }

    public Source getSource() { return source; }
    public void setSource(Source source) {
        this.source = source;
        if (source != null) this.sourceId = source.getSourceId();
    }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public Path getPath() { return path; }
    public void setPath(Path path) {
        this.path = path;
        if (path != null) this.pathId = path.getPathId();
    }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getTrackNumber() { return trackNumber; }
    public void setTrackNumber(String trackNumber) { this.trackNumber = trackNumber; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public String getIdentityType() { return identityType; }
    public void setIdentityType(String identityType) { this.identityType = identityType; }

    public String getTrackStatus() { return trackStatus; }
    public void setTrackStatus(String trackStatus) { this.trackStatus = trackStatus; }

    /** Legacy rows with no stored platform category are exposed as UNKNOWN. */
    public TrackType getPlatformType() { return platformType == null ? TrackType.UNKNOWN : platformType; }
    public void setPlatformType(TrackType platformType) {
        this.platformType = platformType == null ? TrackType.UNKNOWN : platformType;
    }

    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }

    public LocalDateTime getReceiveTime() { return receiveTime; }
    public void setReceiveTime(LocalDateTime receiveTime) { this.receiveTime = receiveTime; }

    @Override
    public String toString() {
        return "Track{" +
                "trackId=" + trackId +
                ", sourceId='" + sourceId + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", pathId=" + pathId +
                ", trackNumber='" + trackNumber + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", identityType='" + identityType + '\'' +
                ", platformType='" + getPlatformType() + '\'' +
                ", trackStatus='" + trackStatus + '\'' +
                ", createdTime=" + createdTime +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
