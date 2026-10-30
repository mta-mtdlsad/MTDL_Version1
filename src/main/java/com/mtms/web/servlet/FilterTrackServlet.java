package com.mtms.web.servlet;

import com.mtms.domain.Track;
import com.mtms.service.TrackAnalyticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FilterTrackServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Track> listOfTracks = null;

        String identity = req.getParameter("identity").toUpperCase();
        String status = req.getParameter("status").toUpperCase();
        int minimumSpeed = Integer.parseInt(req.getParameter("minimumSpeed"));

        if (!identity.isEmpty()){
            listOfTracks = TrackAnalyticsService.showTracksByIdentity(identity);
        } else if (!status.isEmpty()){
            if (status.equals("ACTIVE"))
                listOfTracks = TrackAnalyticsService.showActiveTracks();
            else
                listOfTracks = TrackAnalyticsService.showInactiveTracks();
        } else{
            listOfTracks = TrackAnalyticsService.showTracksByMinimumSpeed(minimumSpeed);
        }

        // print conformation message and list of tracks
        PrintWriter writer = resp.getWriter();
        if (!listOfTracks.isEmpty()){
            writer.print("Displaying tracks filtered tracks");
            writer.print(listOfTracks);
        } else {
            writer.print("Unable to display filtered tracks");
        }


    }
}
