package com.mtms.controller;

import com.mtms.model.FilterParameters;
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
public class ReportController {
    @Autowired
    private TrackService trackService;
    @Autowired
    private AlertService alertService;

    @RequestMapping("/api/statistics")
    private List<Map> getStatistics() {
        List<Map> results = Arrays.asList(
                trackService.getIdentityStatistics(),
                alertService.getSeverityStatistics()
        );
        return results;
    }

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
