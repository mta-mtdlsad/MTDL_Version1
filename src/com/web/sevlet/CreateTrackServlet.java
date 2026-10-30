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
import java.time.LocalDateTime;
import java.util.List;

public class CreateTrackServlet extends HttpServlet {
    private TrackService track_service = new TrackService();
    private AlertService alert_service = new AlertService();
    TrackAnalyticsService track_analytics_service = new TrackAnalyticsService();
    AlertAnalyticsService alert_analytics_service = new AlertAnalyticsService();
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Track track = new Track(
                req.getParameter("trackNumber"),
                Double.parseDouble(req.getParameter("trackLatitude")),
                Double.parseDouble(req.getParameter("trackLongitude")),
                Double.parseDouble(req.getParameter("trackSpeed")),
                req.getParameter("trackIdentity"),
                req.getParameter("trackStatus"),
                LocalDateTime.parse(req.getParameter("trackCreatedTime")));
        if(track_service.processTrack(track)) {
            resp.sendRedirect(req.getContextPath() + "/tracks/list?purpose=tracks");
        }
        else {
            resp.sendRedirect(req.getContextPath() + "/alerts/list?purpose=alerts");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        if(req.getParameter("purpose").equals("statistics")) {
            StringBuilder statistics = new StringBuilder();
            statistics.append("<p>Track Identity Statistics<br>");
            statistics.append(track_analytics_service.identityStatistics());
            statistics.append("<br><br><br>Alert Severity Statistics<br>");
            statistics.append(alert_analytics_service.severityStatistics());
            statistics.append("</p>");
            writer.print(statistics);
        }
        else {
            if (req.getParameter("purpose").equals("tracks")) {
                List<Track> listOfTracks;
                listOfTracks = track_service.displayTracks();
                writer.print(html_util.convertTracksToHtmlTable(listOfTracks));
            } else {
                List<Alert> listOfAlerts;
                listOfAlerts = alert_service.getAlerts();
                writer.print(html_util.convertAlertsToHtmlTable(listOfAlerts));
            }
        }
    }
}
