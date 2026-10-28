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

    //selecting tracks based on the status
    public void getTracksByStatus(String status){
        System.out.println("Status Count :: " +  track_repo.getTracksByStatus(status).stream().count());
        track_repo.getTracksByStatus(status).forEach(System.out::println);
    }
}
