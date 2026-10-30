package com.web.sevlet;

import com.domain.Track;

import com.repository.TrackRepository;

import com.web.util.ApplicationFactory;
import com.web.util.HtmlUtil;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// the Servlet responsible for searching a track by its track number
public class SearchTrackServlet extends BaseServlet {

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
            // Get an instance of TrackRepository using ApplicationFactory
            TrackRepository repository = ApplicationFactory.getTrackRepository();

            // Search for the track using its track number
            Track track = repository.getTrackByNo(trackNumber);

            // Check whether the requested track exists
            if (track == null) {

                // Display a message if no matching track is found
                out.println("<p>Track not found.</p>");

            } else {
                // Display the track number, HtmlUtil.escape() prevents HTML injection,  Cross-Site Scripting (XSS)
                out.println("<p>Track Number: " + HtmlUtil.escape(track.getTrack_number()) + "</p>");

                // Display the track's latitude
                out.println("<p>Latitude: " + track.getLatitude() + "</p>");

                // Display the track's longitude
                out.println("<p>Longitude: " + track.getLongitude() + "</p>");

                // Display the track's speed
                out.println("<p>Speed: " + track.getSpeed() + "</p>");
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
