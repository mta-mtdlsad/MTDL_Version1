package com.mtms.controller;

import com.mtms.model.FilterParameters;
import com.mtms.model.Track;
import com.mtms.service.AlertAnalyticsService;
import com.mtms.service.TrackAnalyticsService;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;

@RestController
public class ReportController {
    @Autowired
    private TrackAnalyticsService trackAnalyticsService;
    @Autowired
    private AlertAnalyticsService alertAnalyticsService;
    @Autowired
    private TrackService trackService;
//
//    @RequestMapping("/api/statistics")
//    private List<String> getStatistics() {
//        List<String> statistics = new ArrayList<>();
//        statistics.add(trackAnalyticsService.identityStatistics().toString());
//        statistics.add(alertAnalyticsService.severityStatistics().toString());
//        return statistics;
//    }

    @RequestMapping("/api/analytics/correlations/{sourceId}/{sequenceNumber}")
    private List<Track> getCorrelatedTracks(@PathVariable String sourceId, @PathVariable String sequenceNumber) {
        FilterParameters filterParams = new FilterParameters();
        filterParams.setSourceId(sourceId);
        filterParams.setSequenceNumber(sequenceNumber);
        List<Track> tracks = trackService.filterTracks(filterParams);
        if(tracks != null) return trackService.correlateTrack(tracks.get(0), null);
        else return tracks;
    }

    @RequestMapping("/api/analytics/correlations")
    private Map<Track, List<Track>> getCorrelatedTracks() {
        return trackService.correlateTracks();
    }
}
