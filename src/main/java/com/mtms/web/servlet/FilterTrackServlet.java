//package com.mtms.web.servlet;
//
//import com.mtms.domain.Track;
//import com.mtms.service.TrackAnalyticsService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//import java.util.Locale;
//
//public class FilterTrackServlet extends BaseServlet {
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        List<Track> listOfTracks = null;
//
//        String identity = req.getParameter("identity").toUpperCase(Locale.ROOT);
//        String status = req.getParameter("status").toUpperCase(Locale.ROOT);
//        int minimumSpeed = Integer.parseInt(req.getParameter("minimumSpeed"));
//
//        if (!identity.equals("DEFAULT")){
//            listOfTracks = TrackAnalyticsService.showTracksByIdentity(identity);
//        } else if (!status.equals("DEFAULT")){
//            if (status.equals("ACTIVE"))
//                listOfTracks = TrackAnalyticsService.showActiveTracks();
//            else
//                listOfTracks = TrackAnalyticsService.showInactiveTracks();
//        }
//        if (minimumSpeed!=0){
//            listOfTracks = TrackAnalyticsService.showTracksByMinimumSpeed(minimumSpeed);
//        }
//        // print conformation message and list of tracks
//        PrintWriter writer = resp.getWriter();
//        if (listOfTracks!=null || !identity.equals("DEFAULT") || !status.equals("DEFAULT") || minimumSpeed!=0){
//            writer.println("Displaying tracks filtered tracks: ");
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
//            writer.print("Unable to display filtered tracks");
//        }
//
//
//    }
//}