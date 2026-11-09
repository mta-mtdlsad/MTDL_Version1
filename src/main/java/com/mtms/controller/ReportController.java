package com.mtms.controller;

import com.mtms.service.TrackAnalyticsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportController {

    //localhost:8080/api/analytics/correlations
    @RequestMapping("/api/analytics/correlations")
    public List<String> getCorrelatedTracks() {
        return TrackAnalyticsService.getCorrelatedTracks();
    }

    //localhost:8080/api/statistics
    @RequestMapping("/api/statistics")
    public List<String> showReportStat() {
        return TrackAnalyticsService.showReportStat();
    }


}