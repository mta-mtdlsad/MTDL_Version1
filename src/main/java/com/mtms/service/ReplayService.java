package com.mtms.service;

import com.mtms.util.ReplaySpeed;
import com.mtms.model.Track;
import com.mtms.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ReplayService {

    @Autowired
    private TrackRepository track_repo;

    public List<Track> replayTracks(
            ReplaySpeed speed,
            LocalDateTime start_time,
            LocalDateTime end_time,
            Consumer<Track> onTrack) {

        // Validate timeframe
        if (start_time.isAfter(end_time)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Retrieve tracks from PostgreSQL
        List<Track> listOfTracks = track_repo.getAllTracksTimeframe(start_time, end_time);

        if (listOfTracks.isEmpty()) {
            System.out.println("No tracks found in the selected timeframe");
            return listOfTracks;
        }
        // Replay each track
        for (int i = 0; i < listOfTracks.size(); i++) {
            Track track = listOfTracks.get(i);
            onTrack.accept(track);
            System.out.println("Replaying: " + track);

            // Delay between tracks
            if (i < listOfTracks.size() - 1) {
                try {
                    Thread.sleep(speed.getMilliseconds());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return listOfTracks;
    }
}