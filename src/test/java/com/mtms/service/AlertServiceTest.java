package com.mtms.service;

import com.mtms.model.Alert;
import com.mtms.model.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.util.ValidationResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertService Class Test")
public class AlertServiceTest {
    @Mock
    private AlertRepository alert_repo;

    @InjectMocks
    private AlertService alertService;

    private Alert alert;

    // Runs before every test
    @BeforeEach
    public void init(TestInfo testInfo, TestReporter testReporter) {
        // New alert for each test
        alert = new Alert();
        // Publish the test info
        testReporter.publishEntry("\nRunning " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }


    // Save an alert
    @Test
    @DisplayName("Test Save Alert Functionality")
    public void saveAlertTest() {
        // Simulate saving the alert
        when(alert_repo.save(alert)).thenReturn(alert);
        // Call the service
        Alert result = alertService.saveAlert(alert);
        // Check that the alert was saved
        assertNotNull(result, "Test Failed: Alert was not saved");
        assertEquals(alert, result, "Test Failed: Saved alert does not match");
        // Check that repository save was called
        verify(alert_repo).save(alert);

    }

    // Retrieve all alerts
    @Test
    @DisplayName("Test Get All Alerts Functionality")
    public void getAlertsTest() {
        // Simulate returning one alert
        when(alert_repo.findAllByOrderByAlertTimeAsc()).thenReturn(List.of(alert));
        // Call the service
        List<Alert> result = alertService.getAlerts();
        // Check that alerts were returned
        assertFalse(result.isEmpty(), "Test Failed: No alerts were returned");
        assertEquals(1, result.size(), "Test Failed: Incorrect number of alerts");
    }

    // Retrieve alerts when database is empty
    @Test
    @DisplayName("Test Get Alerts When Empty")
    public void getAlertsEmptyTest() {
        // Simulate an empty database
        when(alert_repo.findAllByOrderByAlertTimeAsc()).thenReturn(List.of());
        // Call the service
        List<Alert> result = alertService.getAlerts();
        // Check that the list is empty
        assertTrue(result.isEmpty(), "Test Failed: Alert list should be empty");

    }

    // Find an existing alert
    @Test
    @DisplayName("Test Find Alert By ID")
    public void findByIdTest() {
        // Simulate finding the alert
        when(alert_repo.findById(1)).thenReturn(Optional.of(alert));
        // Call the service
        ResponseEntity<Alert> result = alertService.findById(1);
        // Check the response
        assertEquals(HttpStatus.OK, result.getStatusCode(), "Test Failed: Expected HTTP 200");
        assertEquals(alert, result.getBody(), "Test Failed: Incorrect alert returned");

    }

    // Find an alert that does not exist
    @Test
    @DisplayName("Test Find Alert By ID Not Found")
    public void findByIdNotFoundTest() {
        // Simulate missing alert
        when(alert_repo.findById(1)).thenReturn(Optional.empty());
        // Call the service
        ResponseEntity<Alert> result = alertService.findById(1);
        // Check for HTTP 404
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode(), "Test Failed: Expected HTTP 404");
    }

    // Check if an alert exists
    @Test
    @DisplayName("Test Alert Exists By ID")
    public void existsByIdTest() {
        // Simulate an existing alert
        when(alert_repo.existsById(1)).thenReturn(true);
        // Call the service
        boolean result = alertService.existsById(1);
        // Check the result
        assertTrue(result, "Test Failed: Alert should exist");
    }

    // Check if an alert does not exist
    @Test
    @DisplayName("Test Alert Does Not Exist")
    public void notExistsByIdTest() {
        // Simulate a missing alert
        when(alert_repo.existsById(1)).thenReturn(false);
        // Call the service
        boolean result = alertService.existsById(1);
        // Check the result
        assertFalse(result, "Test Failed: Alert should not exist");

    }

    // Delete an alert
    @Test
    @DisplayName("Test Delete Alert By ID")
    public void deleteByIdTest() {
        // Call the service
        alertService.deleteById(1);
        // Check that delete was called
        verify(alert_repo).deleteById(1);
    }

    // Get severity statistics
    @Test
    @DisplayName("Test Get Severity Statistics")
    public void getSeverityStatisticsTest() {
        // Simulate no severity records
        when(alert_repo.countBySeverity()).thenReturn(List.of());
        // Call the service
        Map<String, Integer> result = alertService.getSeverityStatistics();
        // Check severity counts
        assertEquals(0, result.get("HIGH"), "Test Failed: HIGH count is incorrect");
        assertEquals(0, result.get("MEDIUM"), "Test Failed: MEDIUM count is incorrect");
        assertEquals(0, result.get("LOW"), "Test Failed: LOW count is incorrect");
    }

    // Create an alert using a Track
    @Test
    @DisplayName("Test Create Alert With Track")
    public void createAlertWithTrackTest() {
        // Create a track
        Track track = new Track();
        track.setTrackNumber("TRK-10001");
        // Create validation result
        ValidationResult validation = new ValidationResult("Invalid speed", "MEDIUM");
        // Call the service
        alertService.createAlert(track, validation);
        // Check that save was called
        verify(alert_repo).save(any(Alert.class));
    }

    // Create an alert using a track number
    @Test
    @DisplayName("Test Create Alert With Track Number")
    public void createAlertWithTrackNumberTest() {
        // Create validation result
        ValidationResult validation = new ValidationResult("Invalid speed", "HIGH");
        // Call the service
        alertService.createAlert("TRK-10001", validation);
        // Check that save was called
        verify(alert_repo).save(any(Alert.class));
    }

    //Create an alert without a track number
    @Test
    @DisplayName("Test Create Alert Without Track")
    public void createAlertWithoutTrackTest() {
        // Create validation result
        ValidationResult validation = new ValidationResult("Invalid message", "LOW");
        // Call the service
        alertService.createAlert(validation);
        // Check that save was called
        verify(alert_repo).save(any(Alert.class));
    }
}
