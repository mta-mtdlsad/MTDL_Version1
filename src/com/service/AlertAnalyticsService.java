package com.service;

import com.repository.AlertRepository;
import java.util.Map;

public class AlertAnalyticsService {
    private final AlertRepository alertRepository = new AlertRepository();

    //This method will display the alert status report for LOW|MEDIUM|High
    public void displayAlertStatusReport() {
        Map<String, Integer> statistics = alertRepository.getAlertStatistics();


        int low = statistics.getOrDefault("LOW", 0);
        int medium = statistics.getOrDefault("MEDIUM", 0);
        int high = statistics.getOrDefault("HIGH", 0);

        System.out.println("LOW alerts    : " + low);
        System.out.println("MEDIUM alerts : " + medium);
        System.out.println("HIGH alerts   : " + high);

        int total = low + medium + high;

        System.out.println();
        System.out.println("Total alerts  : " + total);
        System.out.println("-----------------------------------------");
    }

    public StringBuilder severityStatistics() {
        Map<String, Integer> statistics = alertRepository.getAlertStatistics();
        int low = statistics.getOrDefault("LOW", 0);
        int medium = statistics.getOrDefault("MEDIUM", 0);
        int high = statistics.getOrDefault("HIGH", 0);
        int total = low + medium + high;

        StringBuilder result = new StringBuilder();
        result.append("LOW alerts    : " + low + "<br>");
        result.append("MEDIUM alerts : " + medium + "<br>");
        result.append("HIGH alerts   : " + high + "<br><br>");
        result.append("Total alerts  : " + total + "<br>");

        return result;
    }
}
