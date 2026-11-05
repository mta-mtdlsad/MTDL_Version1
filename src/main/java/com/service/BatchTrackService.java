package com.service;

import com.domain.Track;
import com.domain.ValidationResult;
import com.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchTrackService {

    private final TrackRepository track_repo;
    private final ValidationService val_service;
    private final AlertService alert_service;

    public BatchTrackService(TrackRepository trackRepo, ValidationService valService, AlertService alertService) {
        track_repo = trackRepo;
        val_service = valService;
        alert_service = alertService;
    }

    public void processBatch(List<Track> tracks) {
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
