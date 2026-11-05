package com.mtms.web.servlet;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

public class AdvancedFiltersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {
            // get all filter values
            String sourceId = request.getParameter("sourceId");
            String trackNumber = request.getParameter("trackNumber");
            String identity = request.getParameter("identity");
            String trackStatus = request.getParameter("status");
            String minSpeedValue = request.getParameter("minSpeed");
            String maxSpeedValue = request.getParameter("maxSpeed");
            String startTimeValue = request.getParameter("startTime");
            String endTimeValue = request.getParameter("endTime");

            // converting empty values into null
            if (sourceId != null && sourceId.trim().isEmpty())
                sourceId = null;

            if (trackNumber != null && trackNumber.trim().isEmpty())
                trackNumber = null;

            if (identity != null && identity.trim().isEmpty())
                identity = null;

            if (trackStatus != null && trackStatus.trim().isEmpty())
                trackStatus = null;

            // converting speed into double
            Double minSpeed = null;
            Double maxSpeed = null;

            if (minSpeedValue != null && !minSpeedValue.trim().isEmpty())
                minSpeed = Double.parseDouble(minSpeedValue.trim());

            if (maxSpeedValue != null && !maxSpeedValue.trim().isEmpty())
                maxSpeed = Double.parseDouble(maxSpeedValue.trim());

            // converting date values into LocalDateTime
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            if (startTimeValue != null && !startTimeValue.trim().isEmpty())
                startTime = LocalDateTime.parse(startTimeValue.trim());

            if (endTimeValue != null && !endTimeValue.trim().isEmpty())
                endTime = LocalDateTime.parse(endTimeValue.trim());

            // checking to ensure at least one filter is selected by user
            if (sourceId == null && trackNumber == null &&
                    identity == null && trackStatus == null &&
                    minSpeed == null && maxSpeed == null &&
                    startTime == null && endTime == null) {

                out.println("Please select at least one filter");
                return;
            }

            // validation to make sure minimum speed is not above maximum speed
            if (minSpeed != null && maxSpeed != null &&
                    minSpeed > maxSpeed) {

                out.println("Minimum speed is more than maximum speed");
                return;
            }

            // validation to make sure that start time is not after end time
            if (startTime != null && endTime != null &&
                    startTime.isAfter(endTime)) {

                out.println("Start time is after end time");
                return;
            }

// calling repo to check matching tracks

            List<Track> tracks = TrackRepository.advancedFilter(sourceId, trackNumber, identity, trackStatus, minSpeed, maxSpeed, startTime, endTime);

            //Displaying search results

            out.println("\n========== ADVANCED FILTER RESULTS ==========\n");

            if (tracks == null || tracks.isEmpty()) {
                out.println("No Matching Tracks Found");
            } else {
                for (Track track : tracks) {
                    out.println("---------------------------------------------");
                    out.println("Track Number      : " + track.getTrackNumber());
                    out.println("Source ID         : " + track.getSourceId());
                    out.println("Latitude          : " + track.getLatitude());
                    out.println("Longitude         : " + track.getLongitude());
                    out.println("Speed             : " + track.getSpeed());
                    out.println("Identity          : " + track.getIdentity());
                    out.println("Track Status      : " + track.getTrackStatus());
                    out.println("Sequence Number   : " + track.getSequenceNumber());
                    out.println("Observation Time  : " + track.getObservationTime());
                }

                out.println("---------------------------------------------");
            }

            out.println("=============================================\n");

        } catch (NumberFormatException e) {

            out.println("Please enter valid speed values");

        } catch (java.time.format.DateTimeParseException e) {

            out.println("Please enter valid date and time values");

        } catch (Exception e) {

            System.out.println("Cannot apply advanced track search: " + e);
            e.printStackTrace();

            out.println("Cannot apply track search");
        }
    }
}