package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Alert;
import com.mtms.domain.Source;
import com.mtms.domain.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.SourceRepository;
import com.mtms.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private final TrackRepository trackRepository;
    private final AlertRepository alertRepository;
    private final SourceRepository sourceRepository;

    public AnalyticsService(TrackRepository trackRepository, AlertRepository alertRepository, SourceRepository sourceRepository) {
        this.trackRepository = trackRepository;
        this.alertRepository = alertRepository;
        this.sourceRepository = sourceRepository;
    }

    public List<Alert> getAlertsBySeverity(String severity) {
        return alertRepository.selectSeverity(severity);
    }

    public Map<String, Object> getOverallStatistics() {
        int totalReceived = 0;
        int acceptedCount = 0;
        int rejectedCount = 0;
        int duplicateCount = 0;

        for (Source s : sourceRepository.findAll()) {
            totalReceived += (s.getReceivedCount() != null ? s.getReceivedCount() : 0);
            acceptedCount += (s.getAcceptedCount() != null ? s.getAcceptedCount() : 0);
            rejectedCount += (s.getRejectedCount() != null ? s.getRejectedCount() : 0);
            duplicateCount += (s.getDuplicateCount() != null ? s.getDuplicateCount() : 0);
        }

        Map<String, Integer> classificationCounts = new HashMap<>();
        classificationCounts.put(MTMSConstants.TRACK_FRIEND, 0);
        classificationCounts.put(MTMSConstants.TRACK_HOSTILE, 0);
        classificationCounts.put(MTMSConstants.TRACK_NEUTRAL, 0);
        classificationCounts.put(MTMSConstants.TRACK_UNKNOWN, 0);

        Map<String, Integer> statusCounts = new HashMap<>();
        statusCounts.put(MTMSConstants.TRACK_ACTIVE, 0);
        statusCounts.put(MTMSConstants.TRACK_INACTIVE, 0);

        List<Track> highSpeedTracks = new ArrayList<>();

        for (Track t : trackRepository.findAll()) {
            if (t.getIdentityType() != null) {
                String idKey = t.getIdentityType().trim().toUpperCase();
                classificationCounts.computeIfPresent(idKey, (k, v) -> v + 1);
            }
            if (t.getTrackStatus() != null) {
                String statusKey = t.getTrackStatus().trim().toUpperCase();
                statusCounts.computeIfPresent(statusKey, (k, v) -> v + 1);
            }
            if (t.getSpeed() > 700.0) {
                highSpeedTracks.add(t);
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProcessed", totalReceived);
        stats.put("acceptedCount", acceptedCount);
        stats.put("rejectedCount", rejectedCount);
        stats.put("duplicateCount", duplicateCount);
        stats.put("classifications", classificationCounts);
        stats.put("statuses", statusCounts);
        stats.put("highSpeedTracks", highSpeedTracks);

        return stats;
    }

    public Map<String, Object> getAlertSummary() {
        int totalAlerts = 0;

        Map<String, Integer> severityMap = new HashMap<>();
        severityMap.put("LOW", 0);
        severityMap.put("MEDIUM", 0);
        severityMap.put("HIGH", 0);

        Map<String, Integer> eventTypeMap = new HashMap<>();

        for (Alert a : alertRepository.findAll()) {
            totalAlerts++;
            if (a.getAlertSeverity() != null) {
                String severityKey = a.getAlertSeverity().trim().toUpperCase();
                severityMap.put(severityKey, severityMap.getOrDefault(severityKey, 0) + 1);
            }
            if (a.getEventType() != null) {
                String eventKey = a.getEventType().trim().toUpperCase();
                eventTypeMap.put(eventKey, eventTypeMap.getOrDefault(eventKey, 0) + 1);
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAlerts", totalAlerts);
        summary.put("severitySummary", severityMap);
        summary.put("eventTypeSummary", eventTypeMap);
        return summary;
    }
}
