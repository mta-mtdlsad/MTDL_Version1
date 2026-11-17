package com.mtms.simulator;

import com.mtms.domain.Track;
import com.mtms.network.UDPSender;
import com.mtms.service.PathService;
import com.mtms.service.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SimulationEngine implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SimulationEngine.class);

    private final TrackSimulator simulator;
    private final UDPSender sender;
    private final String scenarioName;
    private final int pacingDelayMs;
    private volatile boolean running = true;

    public SimulationEngine(String scenarioName, int pacingDelayMs, TrackService trackService, PathService pathService, UDPSender sender) {
        this.simulator = new TrackSimulator(trackService, pathService);
        this.sender = sender;
        this.scenarioName = scenarioName;
        this.pacingDelayMs = pacingDelayMs;
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        log.info(">>> Starting Simulation Scenario: {}", scenarioName);
        List<Track> tracks = simulator.generateTracks();

        for (Track track : tracks) {
            if (!running) {
                log.info(">>> SimulationEngine stopped by user command.");
                break;
            }
            sender.sendTrack(track);

            try {
                Thread.sleep(pacingDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info(">>> SimulationEngine pacing interrupted.");
                break;
            }
        }
        log.info(">>> SimulationEngine stream completed.");
    }
}
