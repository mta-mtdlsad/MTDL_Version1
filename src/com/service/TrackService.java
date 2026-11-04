package com.service;

import com.domain.Track;
import com.domain.ValidationResult;
import com.repository.TrackRepository;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackService {
    TrackRepository track_repo = new TrackRepository();

    /*
    * * * * *
    *  v1.0 *
    * * * * *
    */

    //Using the track and alert repo to validate tracks and raise alerts
    public void processTracks(List<Track> tracks) {

        ValidationService val_service = new ValidationService();
        AlertService alert_service = new AlertService();
        List<ValidationResult> results;
        for (Track track : tracks) {
            results = val_service.validateTrack(track);
            if (results.isEmpty()) {
                try {
                    track_repo.saveTrack(track); //returns confirmation string
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                for (ValidationResult result : results) {
                    alert_service.createAlert(track, result);
                }
            }
        }
    }

    //printing out all tracks using track repo
    public List<Track> displayTracks() {
        List<Track> tracks = track_repo.getAllTracks();
        if (tracks.isEmpty()) {
            System.out.println("No Tracks found in the database");
        }
        System.out.println(tracks);
        return tracks;
    }

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
     *  v1.1 *
     * * * * *
     */

    //clearing all track using track repo
    public void clearTracks() {
        Boolean track = track_repo.clearTracksTable();
        if (track == false) {
            System.out.println("Track table not cleared from the database");
            return;
        }
        track_repo.clearTracksTable();
        System.out.println("Track table cleared successfully"); //prints confirmation string
    }

    /*
     * * * * *
     *  v1.2 *
     * * * * *
     */

    //Using the track and alert repo to validate one track and raise alerts
    public boolean processTrack(Track track) {
        ValidationService val_service = new ValidationService();
        AlertService alert_service = new AlertService();
        List<ValidationResult> results;
        results = val_service.validateTrack(track);
        if (results.isEmpty()) {
            try {
                track_repo.saveTrack(track); //returns confirmation string
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (ValidationResult result : results) {
                alert_service.createAlert(track, result);
                System.out.println(result.toString());
            }
        }
        return false;
    }

    /*
     * * * * *
     *  v1.3 *
     * * * * *
     */

    public boolean validateTrack(Track track) {
        ValidationService val_service = new ValidationService();
        AlertService alert_service = new AlertService();
        List<ValidationResult> results;
        results = val_service.validateTrack(track);
        if (results.isEmpty()) {
            try {
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (ValidationResult result : results) {
                System.out.println(result.toString());
                alert_service.createAlert(track, result);
            }
        }
        return false;
    }

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

    public List<Track> correlateTrack(Track selected_track) {
        String s1 = " != '" + selected_track.getSource_id() + "'";
        String s2 = " = '" + selected_track.getIdentity_type() + "'";
        List<String> filter = Arrays.asList("source_id", "identity_type");
        List<String> filter_values = Arrays.asList(s1, s2);
        List<Track> listOfTracks = track_repo.searchAllTracks(filter, filter_values, null, null, null);

        List<Track> listOfCorrelatedTracks = new ArrayList<>();
        double distance;
        long time;
        for (Track track : listOfTracks) {
            if (Math.abs(selected_track.getSpeed() - track.getSpeed()) <= 100) {
                time = Math.abs(Duration.between(selected_track.getCreated_time(), track.getCreated_time()).getSeconds());
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
}
