package com.service;

import com.domain.ReplaySpeed;
import com.domain.Track;
import com.repository.TrackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public class ReplayService {
    //replaying all tracks and delaying each record by 1 second
    public List<Track> replayTracks(ReplaySpeed speed, LocalDateTime start_time, LocalDateTime end_time, Consumer<Track> onTrack) {
        TrackRepository track_repo = new TrackRepository();
        List<Track> listOfTracks = track_repo.getAllTracksTimeframe(start_time, end_time);
        if (listOfTracks.isEmpty() || listOfTracks == null) {
            System.out.println("No tracks found in the selected timeframe");
            return listOfTracks;
        }
        for (Track track : listOfTracks) {
            onTrack.accept(track);
            System.out.println(track);
            try {
                Thread.sleep(speed.getMilliseconds());
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return listOfTracks;
    }

    //replaying all tracks and delaying each record by 1 second
//    public List<Track> replayTracks(ReplaySpeed speed, LocalDateTime start_time) {
//        return replayTracks(speed, start_time, LocalDateTime.now());
//    }

//    public List<Track> replayTracks(LocalDateTime start_time, LocalDateTime end_time) {
//        try {
//            TrackRepository track_repo = new TrackRepository();
//            List<Track> listOfTracks = track_repo.getAllTracksTimeframe(start_time, end_time);
//            if (listOfTracks.isEmpty() || listOfTracks == null) {
//                System.out.println("No tracks found in the selected timeframe");
//                return listOfTracks;
//            }
//            return listOfTracks;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}