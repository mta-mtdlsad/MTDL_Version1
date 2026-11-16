package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Alert;
import com.mtms.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAllByOrderByAlertTimeDescAlertIdDesc();
    }

    public void createAlert(String trackNumber, String sourceId, String message, String eventType) {
        String severity = chooseSeverity(eventType, message);
        Alert alert = new Alert(trackNumber, sourceId, message, severity, LocalDateTime.now(), eventType);
        System.out.println("Saving alert: "+alert);
        alertRepository.save(alert);
    }

    private String chooseSeverity(String eventType, String message) {
        if (eventType == null) return MTMSConstants.ALERT_LOW;

        String eventLower = eventType.toLowerCase().trim();
        String msgLower = message != null ? message.toLowerCase() : "";

        if (eventLower.contains("malformed") || eventLower.contains("processing failure") || eventLower.contains("disconnected")) {
            return MTMSConstants.ALERT_HIGH;
        }
        if (eventLower.contains("stale")) {
            return MTMSConstants.ALERT_MID;
        }
        if (eventLower.contains("duplicate") || eventLower.contains("out-of-order") || msgLower.contains("repeated") || msgLower.contains("out-of-order")) {
            return MTMSConstants.ALERT_LOW;
        }
        if (eventLower.contains("validation") || eventLower.contains("failure")) {
            if (msgLower.contains("latitude") || msgLower.contains("longitude")) {
                return MTMSConstants.ALERT_HIGH;
            }
            if (msgLower.contains("speed") || msgLower.contains("limit") || msgLower.contains("identity") || msgLower.contains("status") || msgLower.contains("short")) {
                return MTMSConstants.ALERT_MID;
            }
        }
        return MTMSConstants.ALERT_LOW;
    }
}
