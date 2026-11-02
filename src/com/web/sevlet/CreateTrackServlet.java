package com.web.sevlet;

import com.domain.Track;

import com.network.UDPReceiver;
import com.network.UDPSender;
import com.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class CreateTrackServlet extends BaseServlet {
    private TrackService track_service = new TrackService();
    UDPSender sender = new UDPSender();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = req.getParameter("sourceId") + "|" +
                req.getParameter("sequenceNumber") + "|" +
                req.getParameter("trackNumber") + "|" +
                req.getParameter("trackLatitude") + "|" +
                req.getParameter("trackLongitude") + "|" +
                req.getParameter("trackSpeed") + "|" +
                req.getParameter("trackIdentity") + "|" +
                req.getParameter("trackStatus")  + "|" +
                req.getParameter("trackCreatedTime");

        try {
            // Give the receiver time to start
            Thread.sleep(500);
            if (sender.sendMessage(message)) {
                resp.sendRedirect(req.getContextPath() + "/tracks/list?purpose=tracks");
            } else {
                resp.sendRedirect(req.getContextPath() + "/alerts/list?purpose=alerts");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
