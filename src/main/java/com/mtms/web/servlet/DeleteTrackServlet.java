package com.mtms.web.servlet;

import com.mtms.service.TrackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class DeleteTrackServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setRequestEncoding(req);

        // getting track number from the user
        String trackNumber = req.getParameter("trackNumber");

        // checks if track has been deleted
        boolean deleted = TrackService.deleteTrackByNum(trackNumber);

        // print conformation message
        PrintWriter writer = resp.getWriter();
        if (deleted){
            writer.print("Track " + trackNumber + " has been deleted");
        } else {
            writer.print("Track " + trackNumber + " could not be deleted");
        }
    }

}
