package com.web.sevlet;

import com.domain.Track;

import com.repository.TrackRepository;

import com.service.ReplayService;
import com.service.TrackService;
import com.web.util.HtmlUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;

// the Servlet responsible for searching a track by its track number
public class SearchTrackServlet extends BaseServlet {

    TrackService track_service;
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("springContext");
        if (context == null) {
            throw new ServletException("Spring context not initialised");
        }
        track_service = context.getBean(TrackService.class);
    }

    // Handles HTTP GET requests
    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get the track number entered by the user
        String trackNumber = request.getParameter("trackNumber");

        // Validate the track number
        // If it is null or empty, display a 400 Bad Request error
        if (trackNumber == null || trackNumber.isBlank()) {
            showError(request, response, 400, "Track Number is required.");
            return;
        }

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter out = prepareResponse(response);

        // Start the HTML page with the title "Search Result"
        startPage(out, "Search Result");

        try {
            // Search for the track using its track number
            String s = " = '" + trackNumber + "'";
            List<String> filter = Arrays.asList("track_number");
            List<String> filter_values = Arrays.asList(s);
            List<String> sort = Arrays.asList("sequence_number DESC LIMIT 1");
            List<Track> track = track_service.filterTracks(filter, filter_values,  sort,null, null);

            // Check whether the requested track exists
            if (track == null) {

                // Display a message if no matching track is found
                out.println("<p>Track not found.</p>");

            } else {
                out.print(html_util.convertTracksToHtmlTable(track));
            }

        } catch (Exception e) {
            // Set the HTTP response status to 500 (Internal Server Error)
            response.setStatus(500);
            // Display an error message if searching fails
            out.println("<p>Unable to search Track.</p>");
        }
        endPage(out, request);// Finish the HTML page and include any common page elements
    }
}