package com.web.sevlet;

import com.repository.TrackRepository;
import com.service.AlertAnalyticsService;
import com.service.TrackAnalyticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;

public class StatisticsServlet extends BaseServlet {
    TrackAnalyticsService track_analytics_service;
    AlertAnalyticsService alert_analytics_service;

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("springContext");
        if (context == null) {
            throw new ServletException("Spring context not initialised");
        }
        track_analytics_service = context.getBean(TrackAnalyticsService.class);
        alert_analytics_service = context.getBean(AlertAnalyticsService.class);
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter writer = prepareResponse(response);
        StringBuilder statistics = new StringBuilder();

        // Start the HTML page with the title "Statistics Result"
        startPage(writer, "Statistics Result");

        try {
            statistics.append("<p>Track Identity Statistics<br>");
            statistics.append(track_analytics_service.identityStatistics());
            statistics.append("<br><br><br>Alert Severity Statistics<br>");
            statistics.append(alert_analytics_service.severityStatistics());
            statistics.append("</p>");
            writer.print(statistics);

        } catch (Exception e) {
            // Set the HTTP response status to 500 (Internal Server Error)
            response.setStatus(500);
            // Display an error message if retrieval fails
            writer.println("<p>Unable to get Statistics.</p>");
        }
        endPage(writer, request);// Finish the HTML page and include any common page elements
    }
}
