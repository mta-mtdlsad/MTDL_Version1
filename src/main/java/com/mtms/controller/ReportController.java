package com.mtms.controller;

import com.mtms.domain.Track;
import com.mtms.service.TrackAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class ReportController {

    @Autowired
    private TrackAnalyticsService trackAnalyticsService;


    //localhost:8080/api/analytics/correlations
    @RequestMapping("/api/analytics/correlations")
    public String getCorrelatedTracks() {

        List<String> listOfTracks = trackAnalyticsService.getCorrelatedTracks();

        StringBuilder result = new StringBuilder();

        result.append("========== LIST OF CORRELATED TRACKS ==========<pre>");

        if (listOfTracks == null || listOfTracks.isEmpty()) {

            result.append("No tracks available.");

        } else {
            for(String track : listOfTracks){
                result.append(track).append("\n");
                result.append("------------------------------<pre>");
            }
        }
        return result.toString();
    }

    //localhost:8080/api/statistics
    @RequestMapping("/api/statistics")
    public String showReportStat() {

        List<String> listOfTracks = trackAnalyticsService.showReportStat();

        StringBuilder result = new StringBuilder();

        result.append("========== TRACKS STATISTICS ==========<pre>");

        if (listOfTracks == null || listOfTracks.isEmpty()) {

            result.append("No tracks available.");

        } else {
            for(String track : listOfTracks){
                result.append(track).append("\n");
            }
        }
        return result.toString();
    }

}