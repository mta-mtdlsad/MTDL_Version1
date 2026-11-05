package com.web.sevlet;

import com.network.UDPSender;
import com.service.AlertService;
import com.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

public class CreateTrackServlet extends BaseServlet {
    private TrackService track_service;
    UDPSender sender = new UDPSender();

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("springContext");
        if (context == null) {
            throw new ServletException("Spring context not initialised");
        }
        track_service = context.getBean(TrackService.class);
    }

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
