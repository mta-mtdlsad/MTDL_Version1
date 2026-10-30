package com.web.sevlet;
import com.domain.Track;
import com.repository.TrackRepository;

import com.service.TrackAnalyticsService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FilterTracksServlet extends HttpServlet {
    private TrackAnalyticsService track_analytics_service = new TrackAnalyticsService();
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/tracks/list");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Track> listOfTracks;
        if(req.getParameter("purpose").equals("identity")) {
            listOfTracks = track_analytics_service.filterByIdentity(req.getParameter("trackFilterIdentity"));
        }
        else if(req.getParameter("purpose").equals("status")) {
            listOfTracks = track_analytics_service.filterByStatus(req.getParameter("trackFilterStatus"));
        }
        else {
            listOfTracks = track_analytics_service.filterBySpeed(Double.valueOf(req.getParameter("trackFilterSpeed")));
        }

        PrintWriter writer = resp.getWriter();
        writer.print(html_util.convertTracksToHtmlTable(listOfTracks));
    }
}