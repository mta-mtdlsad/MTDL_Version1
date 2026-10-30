package com.mtms.web.servlet;

import com.mtms.domain.Track;
import com.mtms.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/tracks/list")
public class TrackListServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Set response content type
        response.setContentType("text/plain;charset=UTF-8");

        // Retrieve all tracks from TrackService
        List<Track> tracks = TrackService.findAllTracks();

        // Create PrintWriter
        PrintWriter out = response.getWriter();

        out.println("========== LIST OF ALL TRACKS ==========");
        if (tracks == null || tracks.isEmpty()) {
            out.println("No tracks available.");
        } else {
            for (Track track : tracks) {
                out.println("------------------------------");
                out.println("Track ID: " + track.getTrackId());
                out.println("Track Number: " + track.getTrackNumber());
                out.println("Latitude: " + track.getLatitude());
                out.println("Longitude: " + track.getLongitude());
                out.println("Speed: " + track.getSpeed());
                out.println("Identity: " + track.getIdentity());
                out.println("Track Status: " + track.getTrackStatus());
                out.println("Created Time: " + track.getCreatedTime());
                out.println("------------------------------");
            }
        }
        out.close();
    }
}

//        PrintWriter out = response.getWriter();
//
//        System.out.println("<html>");
//        System.out.println("<head> <title>Track List</title></head>");
//        System.out.println("<body>");
//        System.out.println("<h2>List of All Tracks</h2>");
//
//        if (tracks == null || tracks.isEmpty()) {
//            System.out.println("<p>No tracks available</p>");
//        }
//        super.doGet(req, resp);
//    }
//}