package com.service;

import com.repository.TrackRepository;

public class ReplayService {
    public void replayTracks() {
        TrackRepository track_repo = new TrackRepository();
        System.out.println("\n---------- Print all ----------");
        track_repo.getAllTracks().forEach(System.out::println);
    }
}
