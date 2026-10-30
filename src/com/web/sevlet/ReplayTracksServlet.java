package com.web.sevlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.domain.ReplaySpeed;
import com.domain.Track;
import com.service.ReplayService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class ReplayTracksServlet extends HttpServlet {
    ReplayService replayService = new ReplayService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        ReplaySpeed speed = ReplaySpeed.valueOf(req.getParameter("speed"));
//        LocalDateTime startDate = LocalDateTime.parse(req.getParameter("startDate"));
//        LocalDateTime endDate = LocalDateTime.parse(req.getParameter("endDate"));
//        resp.sendRedirect(req.getContextPath() + "/getTracksTimeFrame"
//                + "?startDate=" + URLEncoder.encode(String.valueOf(startDate), StandardCharsets.UTF_8)
//                + "&endDate=" + URLEncoder.encode(String.valueOf(endDate), StandardCharsets.UTF_8)
//        );
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String speed1 = req.getParameter("speed");
        System.out.println("Recieved Speed: " + speed1);
        if(speed1 == null){
            resp.sendError(
                    HttpServletResponse.SC_BAD_REQUEST, "Relay speed missing"
            );
            return;
        }
        ReplaySpeed speed = ReplaySpeed.valueOf(req.getParameter("speed"));
        LocalDateTime startDate = LocalDateTime.parse(req.getParameter("startDate"));
        LocalDateTime endDate = LocalDateTime.parse(req.getParameter("endDate"));
        ArrayList<Track> listOfTracks = new ArrayList<>();
        PrintWriter writer = resp.getWriter();
        try {
            System.out.println("1");
            listOfTracks = (ArrayList<Track>) replayService.replayTracks(speed, startDate, endDate, track -> {
                        writer.println(track);
                        writer.flush();
                    }
            );
            System.out.println("2");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        resp.setContentType("text/plain;charset=UTF-8");
//        PrintWriter writer = resp.getWriter();
//        writer.println("<h3>Replay Results<h3>");
//        writer.println("<pre>");
//        for(Track track: listOfTracks){
//            writer.println(
//                    track.toString()
//                            .replace("&", "&amp;")
//                            .replace("<", "&lt;")
//                            .replace(">", "&gt;")
//                            .replace("\"", "&quot;")
//                            .replace("'", "&#39;")
//            );
//        }
//        writer.println("</pre>");
    }
}