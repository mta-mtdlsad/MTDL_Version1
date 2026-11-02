package com.web.sevlet;
import com.domain.Track;

import com.service.TrackAnalyticsService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FilterTracksServlet extends BaseServlet {
    private TrackAnalyticsService track_analytics_service = new TrackAnalyticsService();
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/tracks/list");
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter writer = prepareResponse(response);

        // Start the HTML page with the title "Alert List Result"
        startPage(writer, "Alert List Result");

        try {
            List<Track> listOfTracks;
            if(request.getParameter("purpose").equals("identity")) {
                listOfTracks = track_analytics_service.filterByIdentity(request.getParameter("trackFilterIdentity"));
            }
            else if(request.getParameter("purpose").equals("status")) {
                listOfTracks = track_analytics_service.filterByStatus(request.getParameter("trackFilterStatus"));
            }
            else {
                listOfTracks = track_analytics_service.filterBySpeed(Double.valueOf(request.getParameter("trackFilterSpeed")));
            }

            writer.print(html_util.convertTracksToHtmlTable(listOfTracks));
        } catch (Exception e) {
            // Set the HTTP response status to 500 (Internal Server Error)
            response.setStatus(500);
            // Display an error message if retrieval fails
            writer.println("<p>Unable to get Statistics.</p>");
        }
        endPage(writer, request);// Finish the HTML page and include any common page elements
    }
}