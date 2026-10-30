package com.mtms.service;

import com.mtms.domain.ReplaySpeed;
import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReplayService {
    private static List<Track> listOfTracks = null;

    // Display tracks ordered by time and added speed control
    public static void replayTracks (ReplaySpeed speed){
        TrackRepository trackRepository = new TrackRepository();
        listOfTracks = trackRepository.tracksOrderedByTime();
        try {
            for (Track track : listOfTracks) {
                Thread.sleep(speed.getReplaySpeed());
                System.out.println(track);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    // Overloading
    // Display tracks ordered by time and added speed control and time frame
    public static List<Track> replayTracks (ReplaySpeed speed, LocalDateTime startTime, LocalDateTime endTime){
        TrackRepository trackRepository = new TrackRepository();
        listOfTracks = trackRepository.tracksFilterdByTime(startTime, endTime);
        try {
            for (Track track : listOfTracks) {
                Thread.sleep(speed.getReplaySpeed());
                System.out.println(track);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        return listOfTracks;
    }

    // Overloading
    // Display tracks ordered by time with a filtered time frame (default NORMAL)
    public static void replayTracks (LocalDateTime startTime, LocalDateTime endTime){
        TrackRepository trackRepository = new TrackRepository();
        listOfTracks = trackRepository.tracksFilterdByTime(startTime, endTime);
        try {
            for (Track track : listOfTracks) {
                Thread.sleep(ReplaySpeed.NORMAL.getReplaySpeed());
                System.out.println(track);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
