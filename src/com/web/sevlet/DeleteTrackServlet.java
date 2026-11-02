package com.web.sevlet;
import com.domain.Alert;
import com.domain.Track;

import com.service.AlertAnalyticsService;
import com.service.AlertService;
import com.service.TrackAnalyticsService;
import com.service.TrackService;
import com.web.util.HtmlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class DeleteTrackServlet extends BaseServlet  {
    private TrackService track_service = new TrackService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = prepareResponse(resp);
        String trackNumber = req.getParameter("trackNumber");
        startPage(out, "Delete Result");
        try{
            if (trackNumber == null || trackNumber.isBlank()) {
                showError(req, resp, 400, "Track Number is required.");
                return;
            }
            if(track_service.deleteTrack(trackNumber)) {
                out.print("<p>Track Deleted Successfully</p>");
            }
            else {
                out.print("<p>Failed to Delete Track</p>");
            }
        }catch (Exception e) {
            resp.setStatus(500);
            out.println("<p>Unable to delete Track.</p>");
        }
        endPage(out, req);
    }
}

