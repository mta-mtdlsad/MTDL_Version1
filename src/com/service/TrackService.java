package com.service;

import com.domain.Track;
import com.domain.ValidationResult;
import com.repository.TrackRepository;

import java.util.List;

public class TrackService {
    TrackRepository track_repo = new TrackRepository();

    //Using the track and alert repo to validate tracks and raise alerts
    public void processTracks(List<Track> tracks) {

        ValidationService val_service = new ValidationService();
        AlertService alert_service = new AlertService();
        List<ValidationResult> results;
        for(Track track : tracks) {
            results = val_service.validateTrack(track);
            if(results.isEmpty()) track_repo.saveTrack(track); //returns confirmation string
            else {
                for(ValidationResult result : results) {
                    alert_service.createAlert(track, result);
                }
            }
        }
    }

    //printing out all tracks using track repo
    public List<Track> displayTracks() {
        return track_repo.getAllTracks();
    }

    //printing out track given the track number using the track repo
    public Track searchTracks(String number) {
        return track_repo.getTrackByNo(number);
    }

    //printing statistics report using the track repo
    public List<String> statistcsReport() {
        return track_repo.getStatistics();
    }

    //deleting a track using track repo
    public void deleteTrack(String number) {
        track_repo.deleteTrackByNo(number); //returns confirmation string
    }
}
