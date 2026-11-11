package com.mtms.controller;

import com.mtms.util.FilterParameters;
import com.mtms.model.Track;
import com.mtms.service.AlertService;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReportController {
    @Autowired
    private TrackService trackService;
    @Autowired
    private AlertService alertService;

    // Returns identity and severity statistics
    @RequestMapping("/statistics")
    private List<Map> getStatistics() {
        List<Map> results = Arrays.asList(
                trackService.getIdentityStatistics(),
                alertService.getSeverityStatistics()
        );
        return results;
    }

    // Returns any correlated tracks for a specific observation (given sourceId and sequenceNumber)
    @RequestMapping("/analytics/correlations/{sourceId}/{sequenceNumber}")
    private List<Track> getCorrelatedTracks(@PathVariable String sourceId, @PathVariable String sequenceNumber) {
        FilterParameters filterParams = new FilterParameters();
        filterParams.setSourceId(sourceId);
        filterParams.setSequenceNumber(sequenceNumber);
        List<Track> tracks = trackService.filterTracks(filterParams);
        if(tracks != null) return trackService.correlateTrack(tracks.get(0), null);
        else return tracks;
    }

    // Returns any correlated tracks
    @RequestMapping("/analytics/correlations")
    private Map<Track, List<Track>> getCorrelatedTracks() {
        return trackService.correlateTracks();
    }
}
