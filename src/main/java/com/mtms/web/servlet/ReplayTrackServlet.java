//package com.mtms.web.servlet;
//
//import com.mtms.domain.ReplaySpeed;
//import com.mtms.domain.Track;
//import com.mtms.service.ReplayService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class ReplayTrackServlet extends BaseServlet {
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        // converting user input to enum
//        ReplaySpeed speed;
//        switch (req.getParameter("speed")) {
//            case "half":
//                speed = ReplaySpeed.HALF;
//            case "double":
//                speed = ReplaySpeed.DOUBLE;
//            default:
//                speed = ReplaySpeed.NORMAL;
//        };
//
//        // retrieving list of tracks with speed
//        List<Track> listOfTracks =  ReplayService.replayTracks(speed,
//                LocalDateTime.parse(req.getParameter("start")),
//                LocalDateTime.parse(req.getParameter("end")));
//
//        // print conformation message and list of tracks
//        PrintWriter writer = resp.getWriter();
//        if (listOfTracks != null){
//            writer.println("Displaying tracks with " + speed + " speed");
//            for (Track track : listOfTracks) {
//                writer.println("------------------------------");
//                writer.println("Track ID: " + track.getTrackId());
//                writer.println("Track Number: " + track.getTrackNumber());
//                writer.println("Latitude: " + track.getLatitude());
//                writer.println("Longitude: " + track.getLongitude());
//                writer.println("Speed: " + track.getSpeed());
//                writer.println("Identity: " + track.getIdentity());
//                writer.println("Track Status: " + track.getTrackStatus());
//                writer.println("Source Id: " + track.getSourceId());
//                writer.println("Sequence Number: " + track.getSequenceNumber());
//                writer.println("Observation Time: " + track.getObservationTime());
//                writer.println("Received Time: " + track.getReceivedTime());
//                writer.println("------------------------------");
//            }
//        } else {
//            writer.print("Tracks cannot be displayed");
//        }
//
//    }
//}