package com.mtms.controller;

import com.mtms.model.Alert;
import com.mtms.service.AlertService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertController Class Test")
public class AlertControllerTest {
    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private Alert alert;


    @BeforeEach
    void setUp(TestInfo testInfo) {
        // New alert
        alert = new Alert();
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Print test name after execution
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Create an alert
    @Test
    @DisplayName("Test Create Alert Functionality")
    void createAlertTest() {
        // Simulate saving an alert
        when(alertService.saveAlert(alert)).thenReturn(alert);
        // Call controller method
        Alert result = alertController.createAlert(alert);
        // Check the result
        assertNotNull(result, "Test Failed: Alert was not created");
        assertEquals(alert, result, "Test Failed: Wrong alert returned");
        // Verify service was called
        verify(alertService).saveAlert(alert);
    }

    // Retrieve all alerts
    @Test
    @DisplayName("Test Get All Alerts Functionality")
    void getAllAlertsTest() {
        // Simulate retrieving alerts
        when(alertService.getAlerts()).thenReturn(List.of(alert));
        // Call controller method
        List<Alert> result = alertController.getAllAlerts();
        // Check the returned list
        assertEquals(1, result.size(), "Test Failed: Incorrect number of alerts");
        assertEquals(alert, result.get(0), "Test Failed: Wrong alert returned");

    }

    // Retrieve alert by ID
    @Test
    @DisplayName("Test Get Alert By ID")
    void getAlertByIdTest() {
        // Simulate finding an alert
        when(alertService.findById(1)).thenReturn(ResponseEntity.ok(alert));
        // Call controller method
        ResponseEntity<Alert> result = alertController.getAlertById(1);
        // Check the returned alert
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Test Failed: Incorrect response status");
        assertEquals(alert, result.getBody(), "Test Failed: Wrong alert returned");

    }
    // Delete an alert
    @Test
    @DisplayName("Test Delete Alert Functionality")
    void deleteAlertTest() {
        // Simulate an existing alert
        when(alertService.existsById(1)).thenReturn(true);
        // Call controller method
        ResponseEntity<Void> result = alertController.deleteAlert(1);
        // Check successful deletion response
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode(), "Test Failed: Alert was not deleted");
        // Verify delete method was called
        verify(alertService).deleteById(1);
    }
}