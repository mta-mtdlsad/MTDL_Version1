package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;

import java.util.List;
import java.util.Map;

public class TrackAnalyticsService {
    static TrackRepository trackRepository = new TrackRepository();
    private static List<Track> listOfTracks = null;

    // Display report statistics
    public static void showReportStat() {

        System.out.println("\nHostile Tracks:");
        showHostileTracks();

        System.out.println("\nHigh Speed Tracks:");
        showHighSpeedTracks();

        System.out.println("\nIdentity Count:");
        showIdentityCount();

        System.out.println("\nActive tracks:");
        showActiveTracks();

        System.out.println("\nInactive tracks:");
        showInactiveTracks();
    }

    // function to show hostile tracks
    public static void showHostileTracks() {
        listOfTracks = trackRepository.getHostileTracks();

        if (listOfTracks != null) {
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        } else {
            System.out.println("No hostile tracks available");
        }
    }

    // function to show high-speed tracks
    public static void showHighSpeedTracks() {
        listOfTracks = trackRepository.getHighSpeedTracks();

        if (listOfTracks != null) {
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        } else
            System.out.println("No available tracks with high speed ");
    }


    // function to show identity counts
    public static void showIdentityCount() {
        Map<String, Integer> counts = trackRepository.getIdentityCount();

        if (counts != null) {
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // function to show tracks by identity
    public static List<Track> showTracksByIdentity(String identity) {
        List<Track> listOfTracks = trackRepository.getTracksByIdentity(identity);

        if (listOfTracks != null) {
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        } else {
            System.out.println("No tracks available");
        }
        return listOfTracks;
    }

    // Retrieve active tracks
    public static List<Track> showActiveTracks() {
        listOfTracks = trackRepository.getActiveTracks();
        if (listOfTracks != null)
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        else
            System.out.println("No available active tracks");
        return listOfTracks;
    }

    // Retrieve inactive tracks
    public static List<Track> showInactiveTracks() {
        listOfTracks = trackRepository.getInactiveTracks();
        if (listOfTracks != null)
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        else
            System.out.println("No available inactive tracks");
        return listOfTracks;
    }

    // Retrieve tracks with minimum speed
    public static List<Track> showTracksByMinimumSpeed(int minimumSpeed) {
        return trackRepository.getTracksByMinimumSpeed(minimumSpeed);
    }

    //retrieve the total number of tracks for each identity
    public Map<String, Integer> getIdentitySummary() {
        return trackRepository.getTrackIdentity();
    }
}