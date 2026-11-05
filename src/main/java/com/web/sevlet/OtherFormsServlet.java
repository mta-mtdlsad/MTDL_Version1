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
import java.util.Arrays;
import java.util.List;

public class OtherFormsServlet extends BaseServlet {
    HtmlUtil html_util = new HtmlUtil();
    TrackService track_service;
    List<Track> listOfTracks = null;

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
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Prepare the HTTP response and obtain the PrintWriter
        PrintWriter writer = prepareResponse(response);
        StringBuilder resultPage = new StringBuilder();

        if(request.getParameter("purpose").equals("correlation")) {
            // Start the HTML page with the title
            startPage(writer, "Correlation Result");

            String s1 = " = '" + request.getParameter("correlationSource") + "'";
            String s2 = " = " + request.getParameter("correlationSequence");
            List<String> filter = Arrays.asList("source_id", "sequence_number");
            List<String> filter_values = Arrays.asList(s1, s2);
            Track selected_track = track_service.filterTracks(filter, filter_values, null, null, null).getFirst();

            listOfTracks = track_service.correlateTrack(selected_track);
            listOfTracks.forEach(System.out::println);
            resultPage.append("<p>Potential Correlation<br><br>");
            resultPage.append(html_util.convertTracksToHtmlTable(listOfTracks));
            resultPage.append("</p>");
        }

        try {
            writer.print(resultPage);
        } catch (Exception e) {
            // Set the HTTP response status to 500 (Internal Server Error)
            response.setStatus(500);
            // Display an error message if retrieval fails
            writer.println("<p>Unable to get Result.</p>");
        }
        endPage(writer, request);// Finish the HTML page and include any common page elements
    }
}