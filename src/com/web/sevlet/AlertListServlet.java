package com.web.sevlet;

import com.domain.Track;
import com.service.TrackService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AlertListServlet  extends HttpServlet {
    private TrackService track_service = new TrackService();
    private HtmlUtil html_util = new HtmlUtil();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        List<Track> listOfTracks;
        listOfTracks = track_service.displayTracks();
        writer.print(html_util.convertTracksToHtmlTable(listOfTracks));
    }
}
