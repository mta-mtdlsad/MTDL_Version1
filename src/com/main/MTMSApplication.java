package com.main;

import com.domain.Track;
import com.repository.TrackRepository;
import com.service.*;
import com.simulator.TrackSimulator;
import com.util.DbConnection;
import java.sql.Connection;
import java.util.List;

public class MTMSApplication {
    public static void main(String[] args) {
        try  {
            TrackSimulator track_sim = new TrackSimulator();
            List<Track> all_tracks = track_sim.getTracks();

            TrackService track_service = new TrackService();
            System.out.println("\n---------- Validate ----------");
            track_service.processTracks(all_tracks);

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

            ReplayService replay_service = new ReplayService();
            replay_service.replayTracks();

            System.out.println("\n---------- Delete ----------");
            track_service.deleteTrack("TRK001");
            track_service.deleteTrack("TRK002");
            track_service.deleteTrack("TRK003");
            track_service.deleteTrack("TRK004");
            track_service.deleteTrack("TRK005");
            track_service.deleteTrack("TRK006");
            track_service.deleteTrack("TRK007");

            System.out.println("\n---------- All Alerts ----------");
            AlertService alert_service = new AlertService();
            alert_service.getAlerts();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
