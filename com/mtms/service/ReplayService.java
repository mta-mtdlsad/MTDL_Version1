package com.mtms.service;


import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import java.util.List;

public class ReplayService {
    private final TrackRepository repository;

    public ReplayService(TrackRepository repository) {
        this.repository = repository;
    }

    // TC-15: Replay
    public void runReplay() {
        System.out.println("\nStarting Track History Replay...");

        // Fetch rows ordered by created_time ASC, track_id ASC
        List<Track> replayLogs = repository.getReplayData();

        if (replayLogs.isEmpty()) {
            System.out.println("Replay explains no data: The datastore contains no tracks.");
            return;
        }

        int count = 0;
        for (Track track : replayLogs) {
            count++;
            System.out.printf("[%d/%d] %s%n", count, replayLogs.size(), track.toString());

            // TC-15:  1-second delay (1000 milliseconds) between track displays
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Replay loop execution was interrupted unexpectedly!");
                Thread.currentThread().interrupt(); // Restore interrupted status flag
                break;
            }
        }
        System.out.println("Replay playback sequence processing complete.");
    }
}
