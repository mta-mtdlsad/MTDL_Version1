package com.service;

import com.domain.Track;
import com.domain.ValidationResult;
import com.repository.TrackRepository;

import java.util.List;
import java.util.Map;

public class TrackService {
    TrackRepository track_repo = new TrackRepository();

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
            }
        }
        return false;
    }

    //printing out all tracks using track repo
    public List<Track> displayTracks() {
        List<Track> tracks = track_repo.getAllTracks();
        if (tracks.isEmpty()) {
            System.out.println("No Tracks found in the database");
        }
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
}
