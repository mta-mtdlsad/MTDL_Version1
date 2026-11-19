package com.mtms.web.rest;

import com.mtms.simulator.TrackSimulator;
import com.mtms.simulator.TrackSimulatorV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
public class SimulationController {

    @Autowired
    private TrackSimulatorV2 simulator;

    @GetMapping("/state")
    public Map<String, Object> getSimulationState() {
        Map<String, Object> response = new HashMap<>();
        response.put("simulationState", simulator.isRunning());
        return response;
    }

    @PostMapping("/control")
    public ResponseEntity<String> controlSimulation(@RequestParam("action") String action) throws Exception {
        if ("START".equalsIgnoreCase(action)) {
            simulator.initializeSimulator("NORMAL", 12345L, 5, 20);
            boolean started = simulator.isRunning();
            if (started) {
                return ResponseEntity.ok("Simulation started successfully.");
            } else {
                return ResponseEntity.ok("Simulation is already running.");
            }
        } else if ("STOP".equalsIgnoreCase(action)) {
            simulator.stopSimulation();
            return ResponseEntity.ok("Simulation stopped successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid action command.");
    }
}
