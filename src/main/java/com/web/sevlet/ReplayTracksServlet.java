package com.web.sevlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.domain.ReplaySpeed;
import com.domain.Track;
import com.service.ReplayService;
import com.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;


public class ReplayTracksServlet extends BaseServlet  {
    ReplayService replayService;

    @Override
    public void init() throws ServletException {
        super.init();
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute("springContext");
        if (context == null) {
            throw new ServletException("Spring context not initialised");
        }
        replayService = context.getBean(ReplayService.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = prepareResponse(resp);;
        ReplaySpeed speed = ReplaySpeed.valueOf(req.getParameter("speed"));
        LocalDateTime startDate = LocalDateTime.parse(req.getParameter("startDate"));
        LocalDateTime endDate = LocalDateTime.parse(req.getParameter("endDate"));
        ArrayList<Track> listOfTracks = new ArrayList<>();
        try {
            if (startDate == null || endDate == null || speed == null) {
                showError(req, resp, 400, "All fields  is required.");
                return;
            }
            listOfTracks = (ArrayList<Track>) replayService.replayTracks(speed, startDate, endDate, track -> {
                        out.println(track);
                        out.flush();
                    }
            );
        } catch (Exception e) {
            resp.setStatus(500);
            out.println("<p>Unable to replay Tracks.</p>");
        }
    }
}