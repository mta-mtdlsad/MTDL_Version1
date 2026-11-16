package com.mtms.web.rest;

import com.mtms.simulator.SimulationLifecycleManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationLifecycleManager simulationLifecycleManager;

    public SimulationController(SimulationLifecycleManager simulationLifecycleManager) {
        this.simulationLifecycleManager = simulationLifecycleManager;
    }

    @GetMapping("/state")
    public Map<String, Object> getSimulationState() {
        Map<String, Object> response = new HashMap<>();
        response.put("simulationState", simulationLifecycleManager.getSimulationState());
        return response;
    }

    @PostMapping("/control")
    public ResponseEntity<String> controlSimulation(@RequestParam("action") String action) {
        if ("START".equalsIgnoreCase(action)) {
            boolean started = simulationLifecycleManager.startSimulation();
            if (started) {
                return ResponseEntity.ok("Simulation started successfully.");
            } else {
                return ResponseEntity.ok("Simulation is already running.");
            }
        } else if ("STOP".equalsIgnoreCase(action)) {
            simulationLifecycleManager.stopSimulation();
            return ResponseEntity.ok("Simulation stopped successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid action command.");
    }
}
