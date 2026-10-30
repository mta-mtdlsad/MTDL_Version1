package com.mtms.web.servlet;

import com.mtms.service.AlertAnalyticsService;
import com.mtms.service.TrackAnalyticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {

    private AlertAnalyticsService alertAnalyticsService = new AlertAnalyticsService();
    private TrackAnalyticsService TrackAnalyticsService = new TrackAnalyticsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Set response content type
        response.setContentType("text/plain;charset=UTF-8");

        // Retrieve statistics from AlertAnalyticsService
        Map<String, Integer> identityCount = TrackAnalyticsService.getIdentitySummary();
        Map<String, Integer> severityCount = alertAnalyticsService.getSeveritySummary();

        // Create PrintWriter
        PrintWriter out = response.getWriter();

        // Display identity statistics
        out.println("========== TRACK IDENTITY STATISTICS ==========");
        out.println("FRIEND: " + identityCount.getOrDefault("FRIEND", 0));
        out.println("NEUTRAL: " + identityCount.getOrDefault("NEUTRAL", 0));
        out.println("HOSTILE: " + identityCount.getOrDefault("HOSTILE", 0));
        out.println("UNKNOWN: " + identityCount.getOrDefault("UNKNOWN", 0));

        // Display severity statistics
        out.println("\n========== ALERT SEVERITY STATISTICS ==========");
        out.println("LOW: " + severityCount.getOrDefault("LOW", 0));
        out.println("MEDIUM: " + severityCount.getOrDefault("MEDIUM", 0));
        out.println("HIGH: " + severityCount.getOrDefault("HIGH", 0));
    }
}