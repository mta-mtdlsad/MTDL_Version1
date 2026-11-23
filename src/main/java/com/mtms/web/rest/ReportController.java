package com.mtms.web.rest;

import com.mtms.constant.MTMSConstants;
import com.mtms.model.Track;
import com.mtms.service.AlertService;
import com.mtms.service.TrackService;
import com.mtms.util.FilterParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReportController {
    @Autowired
    private TrackService trackService;

    /*
     * Finds and groups active tracks from different sources that
     *      likely represent the same object (/api/analytics/correlations).
     *
     * @return A Map pairing each correlated Track with a list of matching Track observations.
     */
    @RequestMapping("/analytics/correlations")
    public Map<Track, List<Track>> getCorrelatedTracks() {
        return trackService.correlateTracks();
    }

    /*
     * Aggregates counts of all stored tracks broken down by
     *      identity type (FRIEND, HOSTILE, NEUTRAL, UNKNOWN)
     *      and operational status (ACTIVE, INACTIVE) (GET /api/statistics).
     *
     * @return A Map containing totalTracks and individual category counts.
     */
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

}
