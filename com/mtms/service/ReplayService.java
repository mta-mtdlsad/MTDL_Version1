package com.mtms.service;

import com.mtms.constants.MTMSConstants;
import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import java.util.List;

public class ReplayService {
    private final TrackRepository repository;

    public ReplayService(TrackRepository repository) {
        this.repository = repository;
    }

    // Original workflow: Replays all tracks using the standard normal delay constant
    public void runReplay() {
        runReplayWithSettings(repository.getReplayData(), MTMSConstants.replayNormal);
    }

    // Time Window workflow: Filters tracks by timestamps and applies a customized constant delay
    public void runReplayWindow(String startTime, String endTime, int delayMilliseconds) {
        System.out.printf("\nStarting Track History Replay window from [%s] to [%s] (Delay: %dms)...%n",
                startTime, endTime, delayMilliseconds);

        List<Track> windowedLogs = repository.selectByTimeWindow(startTime, endTime);
        runReplayWithSettings(windowedLogs, delayMilliseconds);
    }

    private void runReplayWithSettings(List<Track> replayLogs, int delayMilliseconds) {
        if (replayLogs.isEmpty()) {
            System.out.println("Replay explains no data: The datastore contains no tracks matching this window.");
            return;
        }

        int count = 0;
        for (Track track : replayLogs) {
            count++;
            System.out.printf("[%d/%d] %s%n", count, replayLogs.size(), track.toString());

            try {
                Thread.sleep(delayMilliseconds);
            } catch (InterruptedException e) {
                System.err.println("Replay loop execution was interrupted unexpectedly!");
                Thread.currentThread().interrupt(); // Restore interrupted status flag
                break;
            }
        }
        System.out.println("Replay playback sequence processing complete.");
    }
}
