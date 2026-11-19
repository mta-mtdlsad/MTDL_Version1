package com.mtms.service;

import com.mtms.domain.Source;
import com.mtms.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SourceService {
    private static final Logger log = LoggerFactory.getLogger(SourceService.class);
    private final SourceRepository sourceRepository;
    private final AlertService alertService;

    public SourceService(SourceRepository sourceRepository, AlertService alertService) {
        this.sourceRepository = sourceRepository;
        this.alertService = alertService;
    }

    // Fail Counter: only incremented when ValidationService conditions fail
    public void syncSourceMetrics(String sourceId, Integer sequenceNumber, String status) {
        syncSourceMetrics(sourceId, sequenceNumber, status, 0);
    }

    // Atomic database-level upsert preventing race conditions under concurrent UDP traffic
    public void syncSourceMetrics(String sourceId, Integer sequenceNumber, String status, int isFail) {
        if (sourceId == null || !sourceId.matches("^[A-Za-z0-9_-]{2,30}$")) {
            return;
        }

        int seqVal = sequenceNumber != null ? sequenceNumber : 0;
        int isAcc = "ACCEPTED".equals(status) ? 1 : 0;
        int isRej = ("REJECTED".equals(status) || "FAILURE".equals(status)
                || "DUPLICATE".equals(status)) ? 1 : 0;
        int isDup = "DUPLICATE".equals(status) ? 1 : 0;

        sourceRepository.syncSourceMetrics(sourceId, seqVal, isAcc, isRej, isDup, isFail);
    }


    public void evaluateSourceHealth() {
        try {
            List<Source> activeOrStaleSources = sourceRepository.findByStatusActiveOrStale();
            LocalDateTime now = LocalDateTime.now();

            for (Source source : activeOrStaleSources) {
                String sourceId = source.getSourceId();
                LocalDateTime lastReceived = source.getLastReceivedTime();
                String currentStatus = source.getSourceStatus();

                if (lastReceived == null) continue;
                long secondsQuiet = Duration.between(lastReceived, now).getSeconds();

                if (secondsQuiet > 60 && !"DISCONNECTED".equalsIgnoreCase(currentStatus)) {
                    updateSourceStatus(sourceId, "DISCONNECTED");
                    alertService.createAlert(
                            null,
                            sourceId,
                            "Source becomes disconnected: Transmission link dead for 60+ seconds",
                            "Source becomes disconnected"
                    );
                } else if (secondsQuiet > 15 && "ACTIVE".equalsIgnoreCase(currentStatus)) {
                    updateSourceStatus(sourceId, "STALE");
                    alertService.createAlert(
                            null,
                            sourceId,
                            "Source becomes stale: No streaming payloads detected for 15+ seconds",
                            "Source becomes stale"
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error evaluating source health metrics: {}", e.getMessage(), e);
        }
    }

    // Fixed parameter order matching SourceRepository.updateSourceStatus(sourceId, status)
    public void updateSourceStatus(String sourceId, String status) {
        try {
            sourceRepository.updateSourceStatus(sourceId, status);
        } catch (Exception e) {
            log.error("Failed to update source status for {}: {}", sourceId, e.getMessage());
        }
    }
}
