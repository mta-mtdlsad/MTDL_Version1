package com.service;

import com.domain.Track;
import com.repository.TrackRepository;

import java.util.Map;

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

    //printing statistics report using the track repo
    public void statistcsReport() {
        Map<String, Integer> statistics = track_repo.getStatistics();
        int friend = statistics.getOrDefault("FRIEND", 0);
        int hostile = statistics.getOrDefault("HOSTILE", 0);
        int neutral = statistics.getOrDefault("NEUTRAL", 0);
        int unknown = statistics.getOrDefault("UNKNOWN", 0);

        System.out.println("FRIEND tracks    : " + friend);
        System.out.println("HOSTILE tracks : " + hostile);
        System.out.println("NEUTRAL tracks   : " + neutral);
        System.out.println("UNKNOWN tracks   : " + unknown);

        int total = friend + hostile + neutral + unknown;

        System.out.println();
        System.out.println("Total tracks  : " + total);
        System.out.println("-----------------------------------------");
    }
}
