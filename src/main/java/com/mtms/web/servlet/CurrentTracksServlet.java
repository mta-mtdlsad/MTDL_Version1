package com.mtms.web.servlet;


import com.mtms.domain.Track;
import com.mtms.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CurrentTracksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        TrackService trackService = new TrackService();

        List<Track> currentTracks = trackService.getCurrentView();

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        out.println(" ========== LIST OF CURRENT TRACKS ============");

        if (currentTracks == null || currentTracks.isEmpty()) {
            out.println("No tracks available.");
        } else {

            for (Track track : currentTracks) {

                out.println("----------------------------------------");
                out.println("Track ID: " + track.getTrackId());
                out.println("Track Number: " + track.getTrackNumber());
                out.println("Latitude: " + track.getLatitude());
                out.println("Longitude: " + track.getLongitude());
                out.println("Speed: " + track.getSpeed());
                out.println("Identity: " + track.getIdentity());
                out.println("Track Status: " + track.getTrackStatus());
                out.println("Source ID: " + track.getSourceId());
                out.println("Sequence Number: " + track.getSequenceNumber());
                out.println("Observation Time: " + track.getObservationTime());
                out.println("Received Time: " + track.getReceivedTime());
                out.println("----------------------------------------");
            }
        }

        out.close();
    }
}
