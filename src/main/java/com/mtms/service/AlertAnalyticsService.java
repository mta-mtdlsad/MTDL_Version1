//package com.mtms.service;
//
//import com.mtms.repository.AlertRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//public class AlertAnalyticsService {
//    static AlertRepository alertRepository = new AlertRepository();
//
//    //retrieve the total number of alerts for each severity
//    public Map<String, Integer> getSeveritySummary() {
//        return alertRepository.getAlertSeverity();
//    }
//}