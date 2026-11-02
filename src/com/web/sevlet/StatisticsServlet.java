package com.web.sevlet;

import com.service.AlertAnalyticsService;
import com.service.TrackAnalyticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class StatisticsServlet extends HttpServlet {
    TrackAnalyticsService track_analytics_service = new TrackAnalyticsService();
    AlertAnalyticsService alert_analytics_service = new AlertAnalyticsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        StringBuilder statistics = new StringBuilder();
        statistics.append("<p>Track Identity Statistics<br>");
        statistics.append(track_analytics_service.identityStatistics());
        statistics.append("<br><br><br>Alert Severity Statistics<br>");
        statistics.append(alert_analytics_service.severityStatistics());
        statistics.append("</p>");
        writer.print(statistics);
    }
}
