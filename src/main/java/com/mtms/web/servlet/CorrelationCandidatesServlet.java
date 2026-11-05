package com.mtms.web.servlet;

import com.mtms.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CorrelationCandidatesServlet extends BaseServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setRequestEncoding(req);

        // Set response content type
        resp.setContentType("text/plain;charset=UTF-8");

        // get list of matching tracks
        List<String> matchingTracks = TrackService.getCorrelatedTracks();

        // Create PrintWriter
        PrintWriter out = resp.getWriter();

        out.println("========== LIST OF Cross-Source Correlation Candidates ==========");
        if (matchingTracks == null || matchingTracks.isEmpty()) {
            sendBadRequest(resp, "No alert available");
        } else {
            for (String tracks : matchingTracks) {
                out.println(tracks);
                out.println("------------------------------");
            }
        }
        out.close();
    }
}
