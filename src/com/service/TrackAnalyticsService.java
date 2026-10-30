package com.service;

import com.domain.Track;
import com.repository.TrackRepository;

import java.util.List;
import java.util.Map;

public class TrackAnalyticsService {
    TrackRepository track_repo = new TrackRepository();

    //printing out the hostile report
    public void hostileReport() {
        track_repo.getAllHostileTracks().forEach(System.out::println);
    }

    //printing out the high speed report
    public void highSpeedReport(Double speed) {
        track_repo.getTracksBySpeed(speed).forEach(System.out::println);
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

    //printing out the high speed report
    public List<Track> filterBySpeed(Double speed) {
        return track_repo.getTracksBySpeed(speed);
    }

    //printing out the high speed report
    public List<Track> filterByStatus(String status) {
        return track_repo.getTracksByStatus(status);
    }

    //printing out the high speed report
    public List<Track> filterByIdentity(String identity) {
        return track_repo.getTracksByIdentity(identity);
    }

    //printing statistics report using the track repo
    public StringBuilder identityStatistics() {
        Map<String, Integer> statistics = track_repo.getStatistics();
        int friend = statistics.getOrDefault("FRIEND", 0);
        int hostile = statistics.getOrDefault("HOSTILE", 0);
        int neutral = statistics.getOrDefault("NEUTRAL", 0);
        int unknown = statistics.getOrDefault("UNKNOWN", 0);
        int total = friend + hostile + neutral + unknown;

        StringBuilder result = new StringBuilder();
        result.append("FRIEND tracks    : " + friend + "<br>");
        result.append("HOSTILE tracks : " + hostile + "<br>");
        result.append("NEUTRAL tracks   : " + neutral + "<br>");
        result.append("UNKNOWN tracks   : " + unknown + "<br><br>");
        result.append("Total tracks  : " + total + "<br>");

        return result;
    }
}
