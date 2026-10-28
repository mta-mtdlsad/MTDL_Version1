package com.main;

import com.domain.ReplaySpeed;
import com.domain.Track;
import com.service.*;
import com.simulator.TrackSimulator;

import java.sql.Timestamp;
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

            //v1.0
            //Test_V1();

            //v1.1
            Test_V1_1();

            System.out.println("\n---------- Delete ----------");
            track_service.deleteTrack("TRK001");
            track_service.deleteTrack("TRK002");
            track_service.deleteTrack("TRK003");
            track_service.deleteTrack("TRK004");
            track_service.deleteTrack("TRK005");
            track_service.deleteTrack("TRK006");
            track_service.deleteTrack("TRK007");
            track_service.deleteTrack("TRK008");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Version 1.0 tests
    public static void Test_V1() {
        try {
            TrackService track_service = new TrackService();

            System.out.println("\n---------- Print by number ----------");

            track_service.searchTracks("TRK001");
            track_service.searchTracks("TRK00100");

            System.out.println("\n---------- Print all ----------");
            track_service.displayTracks();
            System.out.println("\n---------- Print statistics ----------");
            track_service.statistcsReport();

            TrackAnalyticsService analytics_service = new TrackAnalyticsService();
            System.out.println("\n---------- Print all hostile ----------");
            analytics_service.hostileReport();
            System.out.println("\n---------- Print high speed ----------");
            analytics_service.highSpeedReport();

            System.out.println("\n---------- All Alerts ----------");
            AlertService alert_service = new AlertService();
            alert_service.getAlerts();

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
            LocalDateTime end_time = LocalDateTime.of(2026, 12, 31, 11, 59, 59);
            //System.out.println("\n---------- Replay HALF ----------");
            //replay_service.replayTracks(ReplaySpeed.HALF, start_time, end_time);
            System.out.println("\n---------- Replay NORMAL ----------");
            replay_service.replayTracks(ReplaySpeed.NORMAL, start_time, end_time);
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
