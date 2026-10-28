package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import java.util.List;

public class TrackService {
    private final TrackRepository trackRepository;
    private final ValidationService validationService;
    private final AlertService alertService;

    public TrackService(TrackRepository trackRepository, ValidationService validationService, AlertService alertService) {
        this.trackRepository = trackRepository;
        this.validationService = validationService;
        this.alertService = alertService;
    }

    public void processTrack(Track track) {
        List<String> errors = validationService.getValidationErrors(track);

        if (errors.isEmpty()) {
            trackRepository.insert(track);
        } else {
            // TC-10: One separate Alert saved for every failure
            for (String error : errors) {
                alertService.createAlert(track.track_number, error);
            }
        }
    }

    public void displayAllTracks() {
        List<Track> records = trackRepository.selectAll();
        // TC-18: Handle empty database
        if (records.isEmpty()) {
            System.out.println("Reports show empty result due to empty datastore.");
            return;
        }
        // TC-18: Handle not empty database
        records.forEach(System.out::println);
    }

    // TC-11 and TC-12: search
    public void searchTrack(String trackNumber) {
        List<Track> results = trackRepository.search(trackNumber);
        if (results.isEmpty()) {
            // TC-12: Search missing
            System.out.println("Track Not Found");
        } else {
            // TC-11: Track details displayed
            results.forEach(System.out::println);
        }
    }

    // TC-13: Only Hostile
    public void displayHostileReport() {
        List<Track> hostiles = trackRepository.findByIdentity("HOSTILE");
        if (hostiles.isEmpty()) { // If no hostiles
            System.out.println("No HOSTILE tracks currently documented.");
        } else {
            hostiles.forEach(System.out::println);
        }
    }

    // TC-14: Speed > 700
    public void displayHighSpeedReport() {
        List<Track> highSpeeds = trackRepository.findHighSpeedTracks(700.0);
        if (highSpeeds.isEmpty()) { // If no speed above 700
            System.out.println("No velocities exceeding 700 units recorded.");
        } else {
            highSpeeds.forEach(System.out::println);
        }
    }
}
