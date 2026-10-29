package com.mtms.main;

import com.mtms.constants.MTMSConstants;
import com.mtms.domain.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.TrackRepository;
import com.mtms.service.*;
import com.mtms.simulator.TrackSimulator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MTMSApplication {
    public static void main(String[] args) {
        try {
            System.out.println("=== MTMS MAIN ===");

            // Initializations
            TrackRepository trackRepo = new TrackRepository();
            AlertRepository alertRepo = new AlertRepository();
            AlertAnalyticsService alertAnalyticsService = new AlertAnalyticsService(alertRepo);
            ValidationService validationService = new ValidationService();
            AlertService alertService = new AlertService(alertRepo);
            TrackService trackService = new TrackService(trackRepo, validationService, alertService);
            ReplayService replayService = new ReplayService(trackRepo);
            TrackSimulator simulator = new TrackSimulator();
            TrackAnalyticsService trackAnalyticsService = new TrackAnalyticsService(trackRepo, alertRepo);
            BatchTrackService batchTrackService = new BatchTrackService(trackRepo, validationService, alertService);

            //TIME WINDOW CONFIGURATION
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String testingWindowStart = LocalDateTime.now().format(formatter);


            // TC-18: Check empty database --> Show empty if is.
            System.out.println("\n--- Testing TC-18 (Empty Database State Reports Check) ---");
            trackService.displayAllTracks();
            /*
            // TC-01 through TC-10, and TC-17
            System.out.println("\n--- Processing Feed ---");
            List<Track> inputs = simulator.generateTracks();
            for (Track t : inputs) {
                trackService.processTrack(t);
            }

            // TC-11 & TC-12 (Search Operations)
            System.out.println("\n--- Testing TC-11 (Search Existing: TRK1001) ---");
            trackService.searchTrack("TRK1001");

            System.out.println("\n--- Testing TC-12 (Search Missing: TRK9999) ---");
            trackService.searchTrack("TRK9999");

            // TC-17 (Protection against SQL Injection)
            System.out.println("\n--- Testing TC-17 (SQL Injection Escape Handling Verification) ---");
            trackService.searchTrack("TRK1' OR '1'='1");

            // TC-15 (Replay)
            System.out.println("\n--- Testing TC-15 (Replay) ---");
            replayService.runReplay();

            // Enhanced Replay with delay and window
            String testingWindowEnd = LocalDateTime.now().format(formatter);
            System.out.println("\n--- Testing Replay with timeframe and speed: Double ---");
            replayService.runReplayWindow(testingWindowStart, testingWindowEnd, MTMSConstants.replayDouble);


            // Reports
            trackAnalyticsService.reportStatistics();
            alertAnalyticsService.printAlertSeverityReport(MTMSConstants.alertLOW);

            // Deleting a track
            trackService.removeTrackByNumber("TRK1001");

            // Verify deletion
            System.out.println("\nVerifying database after deletion:");
            trackService.displayAllTracks();

            // Reports
            trackAnalyticsService.reportStatistics();
            */

            // Batch Code
            System.out.println("==========================================");
            System.out.println("\nBatch insertion Starting now:");
            System.out.println("\n--- Processing Feed via Batch Transaction Mode ---");
            List<Track> batch = simulator.generateTracks();
            batchTrackService.processTrackBatch(batch);

            System.out.println("==========================================");

            //Testing functions after batch insertion
            // TC-11 & TC-12 (Search Operations)
            System.out.println("\n--- Testing TC-11 (Search Existing: TRK1001) ---");
            trackService.searchTrack("TRK1001");

            System.out.println("\n--- Testing TC-12 (Search Missing: TRK9999) ---");
            trackService.searchTrack("TRK9999");

            // TC-17 (Protection against SQL Injection)
            System.out.println("\n--- Testing TC-17 (SQL Injection Escape Handling Verification) ---");
            trackService.searchTrack("TRK1' OR '1'='1");

            // TC-15 (Replay)
            System.out.println("\n--- Testing TC-15 (Replay) ---");
            replayService.runReplay();

            // Enhanced Replay with delay and window
            String testingWindowEnd = LocalDateTime.now().format(formatter);
            System.out.println("\n--- Testing Replay with timeframe and speed: Double ---");
            replayService.runReplayWindow(testingWindowStart, testingWindowEnd, MTMSConstants.replayDouble);

            // Reports
            trackAnalyticsService.reportStatistics();
            alertAnalyticsService.printAlertSeverityReport(MTMSConstants.alertLOW);

            // Deleting a track
            trackService.removeTrackByNumber("TRK1001");

            // Verify deletion
            System.out.println("\nVerifying database after deletion:");
            System.out.println("\n--- Testing TC-11 (Search Existing: TRK1001) ---");
            trackService.searchTrack("TRK1001");



        } catch (Exception dbUnavailableOrFault) {
            // TC-16: Detects failures and reports clear explanations when PostgreSQL falls off the grid
            System.err.println("\n[TC-16: Database unavailable]");
            System.err.println("Database context engine unreachable. Verify local PostgreSQL status settings.");
            System.err.println("Error Diagnostics: " + dbUnavailableOrFault.getMessage());
        }
    }
}
