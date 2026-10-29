package com.mtms.service;

import com.mtms.domain.Alert;
import com.mtms.repository.AlertRepository;
import java.util.List;

public class AlertAnalyticsService {
    private final AlertRepository alertRepository;

    public AlertAnalyticsService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }


     // Filters alerts by severity

    public void printAlertSeverityReport(String severity) {
        System.out.printf("\n===== ALERT SEVERITY FILTER REPORT (%s) =====%n", severity);

        List<Alert> matchingAlerts = alertRepository.selectSeverity(severity);



        if (matchingAlerts.isEmpty()) {
            System.out.printf("No alert records matching severity level '%s'.%n", severity);
        } else {
            System.out.println("Total Matches Found: " + matchingAlerts.size());
            System.out.println("Matching Alert Records:");
            for (Alert alert : matchingAlerts) {
                System.out.printf("  [ID: %d] Track: %-10s | Msg: %-30s" +
                                " Severity: %s | Time: %s%n",
                        alert.alert_id,
                        (alert.track_number == null ? "UNKNOWN" : alert.track_number),
                        alert.alert_message,
                        alert.alert_severity,
                        alert.alert_time);
            }
        }
        System.out.println("--------------------------------------------------");
    }
}
