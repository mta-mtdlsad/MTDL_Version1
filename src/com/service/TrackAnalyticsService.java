package com.service;

import com.domain.Track;
import com.repository.TrackRepository;

public class TrackAnalyticsService {
    public void getAnalytics() {

        TrackRepository track_repo = new TrackRepository();


        System.out.println("\n---------- Print all hostile ----------");

        track_repo.getAllHostileTracks().forEach(System.out::println);

        System.out.println("\n---------- Print all by speed ----------");

        track_repo.getTracksBySpeed(700.0).forEach(System.out::println);

        System.out.println("\n---------- Print statistics ----------");

        track_repo.getStatistics();
    }
}
