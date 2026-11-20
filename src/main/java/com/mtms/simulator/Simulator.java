package com.mtms.simulator;

import com.mtms.network.UDPSender;
import com.mtms.network.UDPStartupListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class Simulator {
    @Autowired
    protected UDPStartupListener startupListener;
    @Autowired
    protected UDPSender sender;
    // Controls whether the simulation is running.
    // AtomicBoolean allows the Stop button to safely communicate with the background simulation thread.
    protected final AtomicBoolean running = new AtomicBoolean(false);
    // The background thread used for simulation.
    protected Thread simulationThread;
    protected Random random;

    // Initializes simulator based on selected settings then runs simulation
    public void initializeSimulator(String scenario, long seed, int sourceNum, int tracksPerSource) throws Exception {
        this.random = new Random(seed);
        validateParameters(scenario, sourceNum, tracksPerSource);
        getSourcesAndSequences(sourceNum);
        generatePaths();
        simulationThread = new Thread(
                () -> runSimulation(scenario, sourceNum, tracksPerSource),
                "multi-source-simulator"
        );
        startSimulation();
    }

    // Check the settings before creating the simulation
    protected void validateParameters(String scenario, int sourceNum, int tracksPerSource) {
    }

    // Retrieves sourceIds and their sequenceNumbers if any Sources already exist
    // Generates extra sourceIds + sequenceNumbers if needed
    protected void getSourcesAndSequences(int sourceNum) throws SQLException {
    }
    protected void generatePaths() {}

    // Main loop for limited and continuous simulation
    protected void runSimulation(String scenario, int sourceNum, int tracksPerSource) {
    }

    // Used to display simulation status
    public boolean isRunning() {
        return running.get();
    }

    // Set simulation status and start thread
    protected void startSimulation() {
        running.set(true);
        simulationThread.start();
    }

    // Stop the running simulation.
    public synchronized void stopSimulation() {
        running.set(false);
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }
}
