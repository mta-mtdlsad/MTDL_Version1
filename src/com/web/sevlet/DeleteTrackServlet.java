package com.web.sevlet;
import com.domain.Alert;
import com.domain.Track;

import com.service.AlertAnalyticsService;
import com.service.AlertService;
import com.service.TrackAnalyticsService;
import com.service.TrackService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class DeleteTrackServlet extends HttpServlet {
    private TrackService track_service = new TrackService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        if(track_service.deleteTrack(req.getParameter("trackNumber"))) {
            writer.print("<p>Track Deleted Successfully</p>");
        }
        else {
            writer.print("<p>Failed to Delete Track</p>");
        }
    }
}

