package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;

import java.util.List;

public class TrackService {
    static TrackRepository trackRepository = new TrackRepository();
    static AlertService alertService = new AlertService();
    static TrackAnalyticsService trackAnalyticsService = new TrackAnalyticsService();
    private static List<Track> listOfTracks = null;

    // Validate and process each track
    public static boolean  processTrack(Track track) {

        List<String> errors = ValidationService.validateTrack(track);

        if (errors.isEmpty()) {
            trackRepository.addTrack(track);
            System.out.println("Track " + track.getTrackNumber() + " is Saved");
            return true;
        } else {

            alertService.processAlert(
                    track.getTrackNumber(),
                    String.join(", ", errors),
                    track.getIdentity()
            );

            System.out.println("Track " + track.getTrackNumber() + " is Not Saved");
            return false;
        }

    }

    // Retrieve all tracks
    public static List<Track> findAllTracks() {
        listOfTracks = trackRepository.getAllTracks();
        System.out.println("\nRetrieving all tracks:");
        if (listOfTracks != null)
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        else
            System.out.println("No available tracks");
        return listOfTracks;
    }

    // Find tracks by track number
    public static List<Track> findTrackByNum(String trackNumber) {
        listOfTracks = trackRepository.getTrackByNumber(trackNumber);
        System.out.println("\nRetrieving all tracks with track number " + trackNumber + " :");
        if (listOfTracks != null) {
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        }
        else
            System.out.println("No available tracks with track number " + trackNumber);
        return listOfTracks;
    }

    // Delete tracks by track number
    public static boolean deleteTrackByNum(String trackNumber) {
        System.out.println("\nDeleting tracks with track number " + trackNumber + " :");
        Boolean trackDeleted =  trackRepository.deleteTrackByNumber(trackNumber);
        if (trackDeleted == true) {
            System.out.println("Track number " + trackNumber + " deleted successfully");
            return true;
        } else {
            System.out.println("Track number " + trackNumber + " not available");
            return false;
        }
    }
}