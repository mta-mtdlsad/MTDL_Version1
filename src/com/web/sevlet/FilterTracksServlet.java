package com.web.sevlet;
import com.domain.Track;

import com.service.TrackAnalyticsService;
import com.service.TrackService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterTracksServlet extends BaseServlet {
    private TrackService track_service = new TrackService();
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

        // Start the HTML page with the title "Filter List Result"
        startPage(writer, "Filter List Result");

        Map<String, String> filterParameters = Map.of(
                "sourceId", "source_id = ",
                "trackNumber", "track_number = ",
                "identity", "identity_type = ",
                "status", "track_status = ",
                "minSpeed", "speed >= ",
                "maxSpeed", "speed <= ");

        List<String> filters = new ArrayList<>();
        List<String> filter_values = new ArrayList<>();
        List<String> sort = new ArrayList<>();
        String input_value;
        LocalDateTime start_time = null;
        LocalDateTime end_time = null;

        try {
            for (Map.Entry<String, String> entry : filterParameters.entrySet()) {
                input_value = request.getParameter(entry.getKey());
                if(!input_value.isEmpty()) {
                    filters.add(entry.getValue());
                    if(!entry.getKey().equals("minSpeed") && !entry.getKey().equals("maxSpeed"))
                        input_value = "'" + input_value + "'";
                    filter_values.add(input_value);
                }
            }

            input_value = request.getParameter("observationType");
            if(!input_value.isEmpty()) {
                if(input_value.equals("current")) {
                    start_time = LocalDateTime.now().minusMinutes(5);
                    filters.add("created_time");
                    filter_values.add(" >= ?");
                }
                else {
                    start_time = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
                    end_time = LocalDateTime.now().minusMinutes(5);
                    filters.add("created_time");
                    filters.add("created_time");
                    filter_values.add(" >= ?");
                    filter_values.add(" <= ?");
                }
            }
            else {
                input_value = request.getParameter("startTime");
                if(input_value != null && !input_value.isBlank()){
                    start_time = LocalDateTime.parse(input_value);
                    filters.add("created_time");
                    filter_values.add(" >= ?");
                }
                input_value = request.getParameter("endTime");
                if(input_value != null && !input_value.isBlank()){
                    end_time = LocalDateTime.parse(input_value);
                    filters.add("created_time");
                    filter_values.add(" <= ?");
                }
            }

            if(request.getParameter("timeSort") != null) {
                if(request.getParameter("timeSortDirection").equals("DESC"))
                    sort.add("created_time DESC");
                else
                    sort.add("created_time");
            }

            if(request.getParameter("speedSort") != null) {
                if(request.getParameter("speedSortDirection").equals("DESC"))
                    sort.add("speed DESC");
                else
                    sort.add("speed");
            }

            if(request.getParameter("trackNumberSort") != null) {
                if(request.getParameter("trackNumberSortDirection").equals("DESC"))
                    sort.add("track_number DESC");
                else
                    sort.add("track_number");
            }

            List<Track> listOfTracks = track_service.filterTracks(filters, filter_values, sort, start_time, end_time);

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