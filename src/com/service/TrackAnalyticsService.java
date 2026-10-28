package com.service;

import com.domain.Track;
import com.repository.TrackRepository;

public class TrackAnalyticsService {
    TrackRepository track_repo = new TrackRepository();

    //printing out the hostile report
    public void hostileReport() {
        track_repo.getAllHostileTracks().forEach(System.out::println);
    }

    //printing out the high speed report
    public void highSpeedReport() {
        track_repo.getTracksBySpeed(700.0).forEach(System.out::println);
    }
}
