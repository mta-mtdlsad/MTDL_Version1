package com.mtms.web.rest;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Track;
import com.mtms.service.AnalyticsService;
import com.mtms.service.TrackService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReportController {
    private final TrackService trackService;
    private final AnalyticsService analyticsService;

    public ReportController(TrackService trackService, AnalyticsService analyticsService) {
        this.trackService = trackService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics/correlations")
    public List<Map<String, Object>> getCorrelations() {
        return trackService.getCorrelatedTrackGroups();
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        List<Track> allTracks = trackService.getAllTracks();

        int friendCount = 0;
        int hostileCount = 0;
        int neutralCount = 0;
        int unknownCount = 0;
        int activeCount = 0;
        int inactiveCount = 0;

        for (Track t : allTracks) {
            if (MTMSConstants.TRACK_FRIEND.equalsIgnoreCase(t.getIdentityType())) friendCount++;
            else if (MTMSConstants.TRACK_HOSTILE.equalsIgnoreCase(t.getIdentityType())) hostileCount++;
            else if (MTMSConstants.TRACK_NEUTRAL.equalsIgnoreCase(t.getIdentityType())) neutralCount++;
            else if (MTMSConstants.TRACK_UNKNOWN.equalsIgnoreCase(t.getIdentityType())) unknownCount++;

            if (MTMSConstants.TRACK_ACTIVE.equalsIgnoreCase(t.getTrackStatus())) activeCount++;
            else if (MTMSConstants.TRACK_INACTIVE.equalsIgnoreCase(t.getTrackStatus())) inactiveCount++;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTracks", allTracks.size());
        stats.put("friendCount", friendCount);
        stats.put("hostileCount", hostileCount);
        stats.put("neutralCount", neutralCount);
        stats.put("unknownCount", unknownCount);
        stats.put("activeCount", activeCount);
        stats.put("inactiveCount", inactiveCount);
        return stats;
    }

    @GetMapping("/reports/alerts/summary")
    public Map<String, Object> getAlertSummaryReport() {
        return analyticsService.getAlertSummary();
    }

    @GetMapping("/tracks/latest")
    public List<Track> getLatestTracks() {
        return trackService.getLatestTracksView();
    }

    @GetMapping("/tracks/nearest")
    public List<Map<String, Object>> getNearestTracks() {
        return trackService.getNearestTracksWithDistance();
    }

}
