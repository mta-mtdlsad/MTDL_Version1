package com.mtms.service;

import com.mtms.domain.Alert;
import com.mtms.domain.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.TrackRepository;

import java.util.List;

import com.mtms.constants.MTMSConstants;

public class TrackAnalyticsService {
    private final TrackRepository trackRepository;
    private final AlertRepository alertRepository;

    public TrackAnalyticsService(TrackRepository trackRepository, AlertRepository alertRepository) {
        this.trackRepository = trackRepository;
        this.alertRepository = alertRepository;
    }


    public void reportStatistics() {
        // Count of valid tracks
        int acceptedCount = trackRepository.selectAll().size();

        // Count of invalid tracks
        List<Alert> validationFailures = alertRepository.selectAll();
        int rejectedCount = validationFailures.size();

        // Total count of all tracks
        int totalProcessed = acceptedCount + rejectedCount;

        System.out.println("\n===== Track Reports =====");
        System.out.println("Total Simulation Records Processed: " + totalProcessed);
        System.out.println("Accepted Tracks (Saved to DB):       " + acceptedCount);
        System.out.println("Rejected Tracks (Rejected):   " + rejectedCount);
        System.out.println("\n===== REJECTED TRACKS =====");
        if (validationFailures.isEmpty()) {
            System.out.println("No items were rejected.");
        } else {
            System.out.println("Rejected tracks:");
            for (Alert alert : validationFailures) {
                System.out.printf("Track: %-15s | Reason: %-35s | Severity: %s%n",
                        (alert.track_number == null || alert.track_number.trim().isEmpty() ? "UNKNOWN" : alert.track_number),
                        alert.alert_message,
                        alert.alert_severity);
            }
        }
        System.out.println("--------------------------------------------------");

        System.out.println("\n===== TRACK CLASSIFICATION (ACCEPTED ONLY) =====");
        System.out.println("FRIEND:  " + trackRepository.findByIdentity(MTMSConstants.trackFRIEND).size());
        System.out.println("HOSTILE: " + trackRepository.findByIdentity(MTMSConstants.trackHOSTILE).size());
        System.out.println("NEUTRAL: " + trackRepository.findByIdentity(MTMSConstants.trackNEUTRAL).size());
        System.out.println("UNKNOWN: " + trackRepository.findByIdentity(MTMSConstants.trackUNKNOWN).size());
        System.out.println("--------------------------------------------------");
        displayHighSpeedReport();
        System.out.println("====================================================");
        String status = MTMSConstants.trackACTIVE;
        System.out.printf("\n===== TRACK STATUS FILTER REPORT (%s) =====%n", status);
        List<Track> statusTracks = trackRepository.selectByStatus(status);
        if (statusTracks.isEmpty()) {
            System.out.printf("No tracks registered matching status '%s'.%n", status);
        } else {
            //Total tracks with the status
            System.out.println("Total Matches Found: " + statusTracks.size());
            System.out.println("Matching Tracks:");
            statusTracks.forEach(System.out::println);
        }
        System.out.println("--------------------------------------------------");
    }




    public void displayHighSpeedReport() {
        System.out.println("\n===== FR-14: High speed tracks (>700) =====");
        List<Track> highSpeeds = trackRepository.findHighSpeedTracks(700.0);

        if (highSpeeds.isEmpty()) {
            System.out.println("No tracks exceeding the 700.");
        } else {
            System.out.println("Number of tracks: "+ highSpeeds.size());
            highSpeeds.forEach(System.out::println);
        }
        System.out.println("====================================================");
    }
}
