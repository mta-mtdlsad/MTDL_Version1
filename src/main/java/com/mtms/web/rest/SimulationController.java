package com.mtms.web.rest;

import com.mtms.simulator.TrackSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
public class SimulationController {
    @Autowired
    private TrackSimulator simulator;

    /*
     * Checks whether the background track simulator is
     *      currently active (GET /api/simulator/state).
     *
     * @return A JSON Map with key "simulationState" set to "RUNNING" or "STOPPED".
     */
    @GetMapping("/state")
    public Map<String, Object> getSimulationState() {
        Map<String, Object> response = new HashMap<>();
        // Convert the boolean state into RUNNING or STOPPED
        response.put(
                "simulationState",
                simulator.isRunning() ? "RUNNING" : "STOPPED"
        );
        return response;
    }

    /*
     * Starts or stops the track simulator based on the
     *      requested action command (POST /api/simulator/control).
     *
     * @param action   Command string: "START" or "STOP" (case-insensitive).
     * @param scenario Scenario name passed to initializeSimulator when starting.
     * @return 200 OK with a status message, or 400 Bad Request if the action is unrecognized.
     * @throws Exception if simulator initialization fails.
     */
    @PostMapping("/control")
    public ResponseEntity<String> controlSimulation(
            @RequestParam("action") String action,
            @RequestParam("scenario") String scenario
    ) throws Exception {
        if ("START".equalsIgnoreCase(action)) {
            // Initialise and start the simulator
            simulator.initializeSimulator(scenario, 12345L, 5, 20);
            boolean started = simulator.isRunning();
            if (started) {
                return ResponseEntity.ok(
                        "Simulation started successfully."
                );
            } else {
                return ResponseEntity.ok(
                        "Simulation is already running."
                );
            }
        } else if ("STOP".equalsIgnoreCase(action)) {
            // Stop the simulator
            simulator.stopSimulation();
            return ResponseEntity.ok(
                    "Simulation stopped successfully."
            );
        }
        return ResponseEntity.badRequest()
                .body("Invalid action command.");
    }

}