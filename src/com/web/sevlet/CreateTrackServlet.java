package com.web.sevlet;

import com.domain.Track;

import com.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class CreateTrackServlet extends BaseServlet {
    private TrackService track_service = new TrackService();

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
}
