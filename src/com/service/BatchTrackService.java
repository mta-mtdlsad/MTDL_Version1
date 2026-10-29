package com.service;

import com.domain.Track;
import com.domain.ValidationResult;
import com.repository.TrackRepository;

import java.util.ArrayList;
import java.util.List;

public class BatchTrackService {
    public void processBatch(List<Track> tracks) {
        ValidationService val_service = new ValidationService();
        AlertService alert_service = new AlertService();
        TrackRepository track_repo = new TrackRepository();
        List<ValidationResult> results;
        List<Track> valid_tracks = new ArrayList<>();
        for(Track track : tracks) {
            results = val_service.validateTrack(track);
            if(results.isEmpty()) valid_tracks.add(track);
            else {
                for(ValidationResult result : results) {
                    alert_service.createAlert(track, result);
                }
            }
        }
        try{
            track_repo.saveValidTracksAsTransaction(valid_tracks);
        } catch(Exception e) {
            //e.printStackTrace();
            System.out.println("Failed to add track batch");
        }
    }
}
