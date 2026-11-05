package com.web.sevlet;

import com.domain.Track;
import com.service.TrackService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TrackHistoryServlet extends BaseServlet {
    private TrackService track_service;
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


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter writer = prepareResponse(response);

        // Start the HTML page with the title "Track List Result"
        startPage(writer, "Track List Result by Track Number");

        // Get the track number entered by the user
        String trackNumber = request.getParameter("trackNumber");

        // Validate the track number
        // If it is null or empty, display a 400 Bad Request error
        if (trackNumber == null || trackNumber.isBlank()) {
            showError(request, response, 400, "Track Number is required.");
            return;
        }

        try {
            List<Track> listOfTracks;
            listOfTracks = track_service.getTracksByNumber(trackNumber);
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