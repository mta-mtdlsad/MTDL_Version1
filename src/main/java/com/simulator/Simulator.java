package com.simulator;

import com.network.UDPSender;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Simulator {
    // Controls whether the simulation is running.
    // AtomicBoolean allows the Stop button to safely communicate with the background simulation thread.
    protected final AtomicBoolean running = new AtomicBoolean(false);
    // The background thread used for simulation.
    protected Thread simulationThread;
    protected final UDPSender sender = new UDPSender();
    protected Random random;

    // Initializes simulator based on selected settings then runs simulation
    public void initializeSimulator(String scenario, int mode, long seed, int[] params) throws Exception {
        this.random = new Random(seed);
        if(mode == 0) predefinedSimulation();
        else {
            validateParameters(scenario, mode, params);
            getSourcesAndSequences(params);
            generateWeightedSelectors(scenario, params);
            simulationThread = new Thread(
                    () -> runSimulation(mode, params),
                    "multi-source-simulator"
            );
            startSimulation();
        }
    }

    // Sends predefined message, no random generation
    protected void predefinedSimulation() throws Exception {}

    // Check the settings before creating the simulation
    protected void validateParameters(String scenario, int mode, int[] params) {}

    // Retrieves sourceIds and their sequenceNumbers if any Sources already exist
    // Generates extra sourceIds + sequenceNumbers if needed
    protected void getSourcesAndSequences(int[] params) throws SQLException {}

    // Generates weighted selectors that are used to generate source, identity, and status
    // based on scenario + random probabilities
    protected void generateWeightedSelectors(String scenario, int[] params) {}

    // Main loop for limited and continuous simulation
    protected void runSimulation(int mode, int[] params) {}

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