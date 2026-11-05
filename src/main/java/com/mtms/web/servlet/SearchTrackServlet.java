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

public class SearchTrackServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // getting track number from the user
        String trackNumber = req.getParameter("trackNumber");

        // retrieving tracks with the specified track number
        List<Track> listOfTracks = TrackService.findTrackByNum(trackNumber);

        // print conformation message and list of tracks
        PrintWriter writer = resp.getWriter();

        writer.println("========== LIST OF TRACK NUMBER " + trackNumber + "==========");
        if (listOfTracks == null || listOfTracks.isEmpty()) {
            writer.println("No tracks available.");
        } else {
            for (Track track : listOfTracks) {
                writer.println("------------------------------");
                writer.println("Track ID: " + track.getTrackId());
                writer.println("Track Number: " + track.getTrackNumber());
                writer.println("Latitude: " + track.getLatitude());
                writer.println("Longitude: " + track.getLongitude());
                writer.println("Speed: " + track.getSpeed());
                writer.println("Identity: " + track.getIdentity());
                writer.println("Track Status: " + track.getTrackStatus());
                writer.println("Source Id: " + track.getSourceId());
                writer.println("Sequence Number: " + track.getSequenceNumber());
                writer.println("Observation Time: " + track.getObservationTime());
                writer.println("Received Time: " + track.getReceivedTime());
                writer.println("------------------------------");
            }
        }
        writer.close();
    }
}