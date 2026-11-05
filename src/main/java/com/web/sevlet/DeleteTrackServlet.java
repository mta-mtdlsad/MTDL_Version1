package com.web.sevlet;

import com.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;

public class DeleteTrackServlet extends BaseServlet  {
    private TrackService track_service;

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

