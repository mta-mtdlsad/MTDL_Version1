package com.web.sevlet;

import com.domain.Track;
import com.service.TrackService;
import com.web.util.HtmlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TrackListServlet extends BaseServlet {
    private TrackService track_service = new TrackService();
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter writer = prepareResponse(response);

        // Start the HTML page with the title "Track List Result"
        startPage(writer, "Track List Result");

        try {
            List<Track> listOfTracks;
            listOfTracks = track_service.displayTracks();
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
