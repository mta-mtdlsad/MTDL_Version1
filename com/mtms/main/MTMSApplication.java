package com.mtms.main;

import com.mtms.domain.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.TrackRepository;
import com.mtms.service.*;
import com.mtms.simulator.TrackSimulator;

import java.util.List;

public class MTMSApplication {
    public static void main(String[] args) {
        try {
            System.out.println("=== MTMS MAIN ===");

            // Dependancy Initializations
            TrackRepository trackRepo = new TrackRepository();
            AlertRepository alertRepo = new AlertRepository();
            ValidationService validationService = new ValidationService();
            AlertService alertService = new AlertService(alertRepo);
            TrackService trackService = new TrackService(trackRepo, validationService, alertService);
            ReplayService replayService = new ReplayService(trackRepo);
            TrackSimulator simulator = new TrackSimulator();

            // TC-18: Check empty database --> Show empty if is.
            System.out.println("\n--- Testing TC-18 (Empty Database State Reports Check) ---");
            trackService.displayAllTracks();

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

            // TC-13 (Only Hostile)
            System.out.println("\n--- Testing TC-13 (Hostile Report Filter Check) ---");
            trackService.displayHostileReport();

            // TC-14 (Only above 700)
            System.out.println("\n--- Testing TC-14 (High-Speed Report) ---");
            trackService.displayHighSpeedReport();

            // TC-15 (Replay)
            System.out.println("\n--- Testing TC-15 (Replay) ---");
            replayService.runReplay();

        } catch (Exception dbUnavailableOrFault) {
            // TC-16: Detects failures and reports clear explanations when PostgreSQL falls off the grid
            System.err.println("\n[TC-16: Database unavailable]");
            System.err.println("Database context engine unreachable. Verify local PostgreSQL status settings.");
            System.err.println("Error Diagnostics: " + dbUnavailableOrFault.getMessage());
        }
    }
}
