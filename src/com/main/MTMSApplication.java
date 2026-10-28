package com.main;

import com.domain.Track;
import com.repository.TrackRepository;
import com.service.TrackAnalyticsService;
import com.service.TrackService;
import com.service.ValidationService;
import com.simulator.TrackSimulator;
import com.util.DbConnection;
import java.sql.Connection;
import java.util.List;

public class MTMSApplication {
    public static void main(String[] args) {
        try  {
            Connection connection = DbConnection.getConnection();
            System.out.println("Success");
        }
        catch(Exception e) {
            System.out.println("Fail");
            e.printStackTrace();
        }

        TrackSimulator track_sim = new TrackSimulator();
        List<Track> all_tracks = track_sim.getTracks();

        TrackService track_service = new TrackService();
        track_service.processTracks(all_tracks);

        System.out.println("\n---------- Print by number ----------");

        track_service.searchTracks("TRK001");
        track_service.searchTracks("TRK00100");

        TrackAnalyticsService analytics_service = new TrackAnalyticsService();
        analytics_service.getAnalytics();


        TrackRepository track_repo = new TrackRepository();

        System.out.println("\n---------- Delete ----------");
        System.out.println(track_repo.deleteTrackByNo("T001"));
        System.out.println(track_repo.deleteTrackByNo("T002"));
        System.out.println(track_repo.deleteTrackByNo("T003"));
    }
}
