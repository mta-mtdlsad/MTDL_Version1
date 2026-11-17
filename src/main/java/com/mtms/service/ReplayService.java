package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service simulating historical playback execution sequences.
 * Registered as a Spring Service component.
 * Refactored to align with Spring Data JPA repository method names.
 */
@Service
public class ReplayService {
    private final TrackRepository repository;

    // Instantiates service with target repository via Spring Autowired
    @Autowired
    public ReplayService(TrackRepository repository) {
        this.repository = repository;
    }

    // Replays stored tracks sequentially using half pacing speed
    public void runReplay() {
        runReplayWithSettings(repository.findAllByOrderByCreatedTimeAscTrackIdAsc(), MTMSConstants.REPLAY_HALF);
    }

    // Replays tracks captured within specified time window using custom delay pacing
    public void runReplayWindow(String startTime, String endTime, int delayMilliseconds) {
        System.out.printf("%nStarting Track History Replay window from [%s] to [%s] (Delay: %dms)...%n",
                startTime, endTime, delayMilliseconds);

        LocalDateTime start = LocalDateTime.parse(startTime.replace("T", " "), MTMSConstants.TIMESTAMP_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endTime.replace("T", " "), MTMSConstants.TIMESTAMP_FORMATTER);

        List<Track> windowedLogs = repository.selectByTimeWindow(start, end);
        runReplayWithSettings(windowedLogs, delayMilliseconds);
    }

    // Internal loop printing track entries with configured delay pacing
    private void runReplayWithSettings(List<Track> replayLogs, int delayMilliseconds) {
        if (replayLogs.isEmpty()) {
            System.out.println("Replay explains no data: The database contains no tracks matching this window.");
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
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Replay playback sequence processing complete.");
    }
}
