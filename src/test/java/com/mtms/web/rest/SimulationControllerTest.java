package com.mtms.web.rest;

import com.mtms.simulator.SimulationLifecycleManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulationController Class Test")
public class SimulationControllerTest {

    @Mock
    private SimulationLifecycleManager simulationLifecycleManager;

    @InjectMocks
    private SimulationController simulationController;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Retrieve current simulation state
    @Test
    @DisplayName("Test Get Simulation State")
    void getSimulationStateTest() {
        // Simulate RUNNING state
        when(simulationLifecycleManager.getSimulationState()).thenReturn("RUNNING");

        // Call controller method
        Map<String, Object> response = simulationController.getSimulationState();

        // Check state in response map
        assertEquals("RUNNING", response.get("simulationState"));
    }

    // Start simulation when START action is passed
    @Test
    @DisplayName("Test Start Simulation")
    void startSimulationTest() {
        // Simulate successful start
        when(simulationLifecycleManager.startSimulation()).thenReturn(true);

        // Call controller method
        ResponseEntity<String> response = simulationController.controlSimulation("START");

        // Check HTTP 200 and success message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Simulation started successfully.", response.getBody());
    }

    // Stop simulation when STOP action is passed
    @Test
    @DisplayName("Test Stop Simulation")
    void stopSimulationTest() {
        // Call controller method
        ResponseEntity<String> response = simulationController.controlSimulation("STOP");

        // Check HTTP 200 and verify stopSimulation was called
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(simulationLifecycleManager).stopSimulation();
    }

    // Return HTTP 400 for unrecognized action command
    @Test
    @DisplayName("Test Invalid Simulation Command")
    void invalidActionTest() {
        // Call controller with invalid action
        ResponseEntity<String> response = simulationController.controlSimulation("PAUSE");

        // Check for HTTP 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
