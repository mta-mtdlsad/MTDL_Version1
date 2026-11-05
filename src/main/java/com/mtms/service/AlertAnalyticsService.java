package com.mtms.service;

import com.mtms.repository.AlertRepository;
import java.util.Map;

public class AlertAnalyticsService {
    static AlertRepository alertRepository = new AlertRepository();

    //retrieve the total number of alerts for each severity
    public Map<String, Integer> getSeveritySummary() {
        return alertRepository.getAlertSeverity();
    }
}