package com.mtms.simulator;

import com.mtms.network.UDPSender;
import com.mtms.service.PathService;
import com.mtms.service.SourceService;
import com.mtms.service.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SimulationLifecycleManager {
    private static final Logger log = LoggerFactory.getLogger(SimulationLifecycleManager.class);

    private final TrackService trackService;
    private final SourceService sourceService;
    private final PathService pathService;
    private final UDPSender udpSender;

    private ExecutorService simulationExecutor;
    private ScheduledExecutorService healthScheduler;
    private SimulationEngine activeEngine;
    private volatile String simulationState = "STOPPED";

    public SimulationLifecycleManager(TrackService trackService, SourceService sourceService, PathService pathService, UDPSender udpSender) {
        this.trackService = trackService;
        this.sourceService = sourceService;
        this.pathService = pathService;
        this.udpSender = udpSender;
    }

    public String getSimulationState() {
        return simulationState;
    }

    public synchronized boolean startSimulation() {
        if ("RUNNING".equalsIgnoreCase(simulationState)) {
            return false;
        }

        simulationState = "RUNNING";

        activeEngine = new SimulationEngine("Normal Traffic", 50, trackService, pathService, udpSender);
        simulationExecutor = Executors.newCachedThreadPool();

        simulationExecutor.submit(activeEngine);

        healthScheduler = Executors.newSingleThreadScheduledExecutor();
        healthScheduler.scheduleAtFixedRate(sourceService::evaluateSourceHealth, 5, 5, TimeUnit.SECONDS);

        simulationExecutor.submit(() -> {
            try {
                Thread.sleep(6000);
                if (!"RUNNING".equalsIgnoreCase(simulationState)) return;

                log.info("[TRAFFIC DROP] Halting simulation streams for 16s to trigger STALE status thresholds...");
                Thread.sleep(16000);

                if (!"RUNNING".equalsIgnoreCase(simulationState)) return;

                log.info("[TRAFFIC DROP] Halting streams further to hit 61s total quiet and trigger DISCONNECTED thresholds...");
                Thread.sleep(45000);

                log.info("[SIMULATION ENDS HERE]");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return true;
    }

    public synchronized void stopSimulation() {
        simulationState = "STOPPED";

        if (activeEngine != null) {
            activeEngine.stop();
        }
        if (simulationExecutor != null) {
            shutdownExecutor(simulationExecutor);
        }
        if (healthScheduler != null) {
            shutdownExecutor(healthScheduler);
        }
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate cleanly.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
