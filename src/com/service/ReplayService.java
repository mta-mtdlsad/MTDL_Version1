package com.service;

import com.domain.ReplaySpeed;
import com.domain.Track;
import com.repository.TrackRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class ReplayService {
    //replaying all tracks and delaying each record by 1 second
    public void replayTracks(ReplaySpeed speed, LocalDateTime start_time, LocalDateTime end_time) {
        try {
            TrackRepository track_repo = new TrackRepository();
            List<Track> listOfTracks = track_repo.getAllTracksTimeframe(start_time, end_time);
            for(Track track : listOfTracks) {
                System.out.println(track);
                Thread.sleep(speed.getMilliseconds());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
