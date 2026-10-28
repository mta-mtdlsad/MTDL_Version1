package com.service;

import com.domain.Track;
import com.repository.TrackRepository;

import java.util.List;

public class TrackService {
    TrackRepository track_repo = new TrackRepository();

    public void processTracks(List<Track> tracks) {
        System.out.println("\n---------- Validate ----------");

        ValidationService val_service = new ValidationService();
        AlertService alert_service = new AlertService();
        List<String> val;
        for(Track t : tracks) {
            val = val_service.validateTrack(t);
            System.out.println(val);
            if(val.isEmpty()) track_repo.saveTrack(t);
            /*
            else {
                for(String alert : val) {
                    //alert_service(t, val);
                }
            }*/
        }
    }
    public void displayTracks() {
        System.out.println("\n---------- Print all ----------");
        track_repo.getAllTracks().forEach(System.out::println);
    }

    public void searchTracks(String number) {
        Track track = track_repo.getTrackByNo(number);
        if(track != null) System.out.print(track);
    }
}
