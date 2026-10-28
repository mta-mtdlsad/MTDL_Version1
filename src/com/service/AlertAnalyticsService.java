package com.service;

import com.repository.AlertRepository;
import java.util.Map;

public class AlertAnalyticsService {
    private final AlertRepository alertRepository = new AlertRepository();

    //
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
}
