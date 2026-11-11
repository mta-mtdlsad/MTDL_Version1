//
//package com.mtms.web.servlet;
//
//import com.mtms.domain.Track;
//import com.mtms.service.TrackService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
//public class TrackHistoryServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        // Get track number entered by the user
//        String trackNumber = request.getParameter("trackNumber");
//
//        PrintWriter out = response.getWriter();
//
//        // Validate input
//        if (trackNumber == null || trackNumber.trim().isEmpty()) {
//            out.println("Please enter a track number.");
//            return;
//        }
//        trackNumber = trackNumber.trim();
//
//        // Create TrackService instance
//        TrackService trackService = new TrackService();
//
//        // Find the track matching the entered track number
//        List<Track> tracks = TrackService.findTrackByNum(trackNumber);
//
//        // Check whether the track exists
//        if (tracks == null || tracks.isEmpty()) {
//            out.println("Track Number " + trackNumber + " Could Not Be Found.");
//            return;
//        }
//
//        // Get source ID from the matching track
//        String sourceId = tracks.get(0).getSourceId();
//
//        // Retrieve complete history
//        List<Track> trackHistory = trackService.getFullHistory(sourceId, trackNumber);
//
//
//        out.println("========== FULL HISTORY OF TRACK : " + trackNumber + " ==========");
//
//
//        if (trackHistory == null && trackHistory.isEmpty()) {
//            out.println("No tracks available.");
//        } else {
//            System.out.println("Source ID: " + sourceId);
//            for (Track track : trackHistory) {
//                out.println("Track Number: " + track.getTrackNumber());
//                out.println("Source ID: " + track.getSourceId());
//                out.println("Latitude: " + track.getLatitude());
//                out.println("Longitude: " + track.getLongitude());
//                out.println("Speed: " + track.getSpeed());
//                out.println("Identity: " + track.getIdentity());
//                out.println("Track Status: " + track.getTrackStatus());
//                out.println("Sequence Number: " + track.getSequenceNumber());
//                out.println("Observation Time: " + track.getObservationTime());
//                out.println("------------------------------");
//            }
//        }
//        out.close();
//
//    }
//}