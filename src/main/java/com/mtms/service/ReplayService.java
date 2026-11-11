package com.mtms.service;

import com.mtms.model.ReplaySpeed;
import com.mtms.model.Track;
import com.mtms.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ReplayService {
//    @Autowired
//    private TrackRepository track_repo;
//
//    //replaying all tracks and delaying each record by 1 second
//    public List<Track> replayTracks(ReplaySpeed speed, LocalDateTime start_time, LocalDateTime end_time, Consumer<Track> onTrack) {
//        List<Track> listOfTracks = track_repo.getAllTracksTimeframe(start_time, end_time);
//        if (listOfTracks.isEmpty() || listOfTracks == null) {
//            System.out.println("No tracks found in the selected timeframe");
//            return listOfTracks;
//        }
//        for (Track track : listOfTracks) {
//            onTrack.accept(track);
//            System.out.println(track);
//            try {
//                Thread.sleep(speed.getMilliseconds());
//            } catch (Exception e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//        return listOfTracks;
//    }
}
