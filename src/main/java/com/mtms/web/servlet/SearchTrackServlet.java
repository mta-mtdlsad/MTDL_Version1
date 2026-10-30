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
        if (!listOfTracks.isEmpty()){
            writer.print("Displaying tracks with track number " + trackNumber);
            writer.print(listOfTracks);
        } else {
            writer.print("Tracks with track number " + trackNumber + " could not be found");
        }
    }
}
