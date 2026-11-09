package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.model.ValidationResult;
import com.mtms.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrackService {
    @Autowired
    private TrackRepository track_repo;

    /*
     * * * * *
     *  v1.0 *
     * * * * *
     */

    //printing out track given the track number using the track repo
    public Track searchTracks(String number) {
        Track track = track_repo.getTrackByNo(number);
        if (track == null) {
            System.out.println("Track not found in the database");
        }
        return track;
    }

    //deleting a track using track repo
    public Boolean deleteTrack(String number) {
        if (searchTracks(number) == null) {
            System.out.println("Track not found in the database");
            return false;
        } else {
            track_repo.deleteTrackByNo(number);
            System.out.println("Track " + number + " Deleted successfully"); //prints confirmation string
            return true;
        }
    }

    /*
     * * * * *
     *  v1.3 *
     * * * * *
     */

    public boolean saveTrack(Track track) {
        try {
            track_repo.saveTrack(track); //returns confirmation string
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Track> filterTracks(List<String> filters, List<String> filter_values, List<String> sort,
                                    LocalDateTime start_time, LocalDateTime end_time) {
        return track_repo.searchAllTracks(filters, filter_values, sort, start_time, end_time);
    }

    public int getHighestSequence(String sourceId) throws SQLException {
        return track_repo.getHighestSequence(sourceId);
    }

    public List<Track> correlateTrack(Track selected_track, List<Track> search_pool) {
        String s1 = " != '" + selected_track.getSourceId() + "'";
        String s2 = " = '" + selected_track.getIdentityType() + "'";
        List<String> filter = Arrays.asList("source_id", "identity_type");
        List<String> filter_values = Arrays.asList(s1, s2);
        List<Track> listOfTracks = track_repo.searchAllTracks(filter, filter_values, null, null, null);

        List<Track> listOfCorrelatedTracks = new ArrayList<>();
        double distance;
        long time;
        for (Track track : listOfTracks) {
            if(search_pool != null) {
                if(search_pool.isEmpty() || !search_pool.contains(track)) continue;
            }
            if (Math.abs(selected_track.getSpeed() - track.getSpeed()) <= 100) {
                time = Math.abs(Duration.between(selected_track.getCreatedTime(), track.getCreatedTime()).getSeconds());
                if (time <= 30) {
                    distance = calculate_distance(selected_track.getLatitude(), selected_track.getLongitude(),
                            track.getLatitude(), track.getLongitude());
                    if (distance <= 2)
                        listOfCorrelatedTracks.add(track);
                }
            }
        }
        return listOfCorrelatedTracks;
    }

    public double calculate_distance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;

        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Differences in coordinates
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine core math
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Return final distance in kilometers
        return EARTH_RADIUS_KM * c;
    }

    public Map<Track, List<Track>> correlateTracks() {
        List<String> filter = Arrays.asList("track_status");
        List<String> filter_values = Arrays.asList(" = 'ACTIVE'");
        List<Track> allActiveTracks = track_repo.searchAllTracks(filter, filter_values, null, null, null);
        Map<Track, List<Track>> correlationMap = new HashMap<>();
        List<Track> updatedActiveTracks = new ArrayList<>(allActiveTracks);
        List<Track> listOfTracks;
        for(Track track : allActiveTracks) {
            if(!updatedActiveTracks.contains(track)) continue;
            listOfTracks = correlateTrack(track, updatedActiveTracks);
            if (!listOfTracks.isEmpty()) {
                correlationMap.put(track, listOfTracks);
                for(Track correlatedTrack : listOfTracks)
                    updatedActiveTracks.remove(correlatedTrack);
            }
            updatedActiveTracks.remove(track);
            if(updatedActiveTracks.isEmpty()) break;
        }
        return correlationMap;
    }
}
