package com.mtms.web.servlet;

import com.mtms.domain.Track;
import com.mtms.service.TrackService;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class CreateTrackServlet  extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setRequestEncoding(req);
        // creating new track object from the
        Track track = new Track(
                Integer.parseInt(req.getParameter("trackId")),
                req.getParameter("trackNumber"),
                Double.parseDouble(req.getParameter("latitude")),
                Double.parseDouble(req.getParameter("longtitude")),
                Float.parseFloat(req.getParameter("speed")),
                req.getParameter("identity"),
                req.getParameter("trackStatus"),
                LocalDateTime.now()
        );

        // processing and validating track
        boolean saved = TrackService.processTrack(track);

        // print conformation message
        PrintWriter writer = resp.getWriter();
        if (saved){
            writer.print("Track details saved");
            writer.print("Track: " + track);
        } else {
            writer.print("Track invalid, an alert has been created");
        }

    }
}
