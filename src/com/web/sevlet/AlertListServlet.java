package com.web.sevlet;

import com.domain.Alert;
import com.service.AlertService;
import com.web.util.HtmlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AlertListServlet extends BaseServlet {
    private AlertService alert_service = new AlertService();
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter writer = prepareResponse(response);

        // Start the HTML page with the title "Alert List Result"
        startPage(writer, "Alert List Result");

        try {
            List<Alert> listOfAlerts;
            listOfAlerts = alert_service.getAlerts();
            writer.print(html_util.convertAlertsToHtmlTable(listOfAlerts));

        } catch (Exception e) {
            // Set the HTTP response status to 500 (Internal Server Error)
            response.setStatus(500);
            // Display an error message if retrieval fails
            writer.println("<p>Unable to get Statistics.</p>");
        }
        endPage(writer, request);// Finish the HTML page and include any common page elements
    }
}
