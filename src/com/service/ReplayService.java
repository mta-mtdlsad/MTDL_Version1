package com.service;

import com.domain.Track;
import com.repository.TrackRepository;
import java.util.List;

public class ReplayService {
    //replaying all tracks and delaying each record by 1 second
    public void replayTracks() throws InterruptedException {
        TrackRepository track_repo = new TrackRepository();
        System.out.println("\n---------- Replay ----------");
        List<Track> listOfTracks = track_repo.getAllTracks();
        for(Track track : listOfTracks) {
            System.out.println(track);
            Thread.sleep(1000);
        }
    }
}
