package com.mtms.web.servlet;

import com.mtms.domain.Track;
import com.mtms.service.TrackService;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Locale;

public class CreateTracksServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setRequestEncoding(req);
        // creating new track object from the
        Track track = new Track(
                req.getParameter("trackNumber"),
                Double.parseDouble(req.getParameter("latitude")),
                Double.parseDouble(req.getParameter("longitude")),
                Double.parseDouble(req.getParameter("speedNumber")),
                req.getParameter("identity").toUpperCase(Locale.ROOT),
                req.getParameter("status").toUpperCase(Locale.ROOT),
                req.getParameter("sourceId"),
                Integer.parseInt(req.getParameter("sequenceNumber")),
                LocalDateTime.parse(req.getParameter("observationTime")),
                LocalDateTime.now()
        );


        // processing and validating track
        boolean saved = TrackService.processTrack(track);

        // print conformation message
        PrintWriter out = resp.getWriter();
        if (saved){
            out.println("===== Track details saved =====");
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
        } else {
            out.print("Track invalid, an alert has been created");
        }

    }
}
