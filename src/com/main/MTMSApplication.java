package com.main;

import com.domain.ReplaySpeed;
import com.domain.Track;
import com.service.*;
import com.simulator.TrackSimulator;
import java.time.LocalDateTime;
import java.util.List;

public class MTMSApplication {
    public static void main(String[] args) {
        try {
            TrackSimulator track_sim = new TrackSimulator();
            List<Track> all_tracks = track_sim.getTracks();

            TrackService track_service = new TrackService();
            //System.out.println("\n---------- Validate ----------");
            track_service.processTracks(all_tracks);

            //use enhanced simulation function
            List<Track> all_tracks_enhanced = track_sim.getTracksEnhanced();
            track_service.processTracks(all_tracks_enhanced);

            //v1.0
            Test_V1();

            //v1.1
            Test_V1_1();

            System.out.println("\n---------- Delete ----------");
            track_service.deleteTrack("TRK001");

            System.out.println("\n---------- Clear tables ----------");
            AlertService alert_service = new AlertService();
            track_service.clearTracks();
            alert_service.clearAlerts();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Version 1.0 tests
    public static void Test_V1() {
        try {
            TrackService track_service = new TrackService();
            TrackAnalyticsService analytics_service = new TrackAnalyticsService();

            System.out.println("\n---------- Print by number ----------");

            String track_number = "TRK001";
            Track track = track_service.searchTracks(track_number);
            if(track != null) System.out.println(track);
            else System.out.println("Track with number " + track_number + " not found");

            track_number = "TRK00100";
            track = track_service.searchTracks(track_number);
            if(track != null) System.out.println(track);
            else System.out.println("Track with number " + track_number + " not found");

            System.out.println("\n---------- Print all ----------");
            track_service.displayTracks().forEach(System.out::println);
            System.out.println("\n---------- Print statistics ----------");
            analytics_service.statistcsReport();

            System.out.println("\n---------- Print all hostile ----------");
            analytics_service.hostileReport();
            System.out.println("\n---------- Print high speed ----------");
            analytics_service.highSpeedReport();

            System.out.println("\n---------- All Alerts ----------");
            AlertService alert_service = new AlertService();
            alert_service.getAlerts().forEach(System.out::println);

            //System.out.println("\n---------- Replay ----------");
            //ReplayService replay_service = new ReplayService();
            //replay_service.replayTracks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Test_V1_1() {
        try {
            // E-02 :: Replay Speed Control
            // E-03 :: Replay Time Window
            ReplayService replay_service = new ReplayService();
            LocalDateTime start_time = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
            LocalDateTime end_time = LocalDateTime.of(2026, 1, 2, 0, 0, 0);
            //System.out.println("\n---------- Replay HALF ----------");
            //replay_service.replayTracks(ReplaySpeed.HALF, start_time, end_time);
            //System.out.println("\n---------- Replay NORMAL ----------");
            //replay_service.replayTracks(ReplaySpeed.NORMAL, start_time, end_time);
            System.out.println("\n---------- Replay DOUBLE ----------");
            replay_service.replayTracks(ReplaySpeed.DOUBLE, start_time, end_time);

            // E-04 :: Track Status Filter
            TrackAnalyticsService analytics_service = new TrackAnalyticsService();
            System.out.println("\n---------- Tracks that are ACTIVE ----------");
            analytics_service.getTracksByStatus("ACTIVE");

            // E-05 :: Display Alert Severity Summary
            System.out.println("\n---------- Alerts Severity Summary ----------");
            AlertAnalyticsService alert_analytics_service = new AlertAnalyticsService();
            alert_analytics_service.displayAlertStatusReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
