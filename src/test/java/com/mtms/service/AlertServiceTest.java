package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Alert;
import com.mtms.repository.AlertRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertService Class Test")
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Alert alert;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        // Initialize a sample alert
        alert = new Alert();
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Retrieve all alerts ordered by time and ID descending
    @Test
    @DisplayName("Test Get All Alerts")
    void getAllAlertsTest() {
        // Simulate repository returning one alert
        when(alertRepository.findAllByOrderByAlertTimeDescAlertIdDesc()).thenReturn(List.of(alert));

        // Call the service method
        List<Alert> result = alertService.getAllAlerts();

        // Check the returned list
        assertEquals(1, result.size(), "Test Failed: Incorrect alert count");
        assertEquals(alert, result.get(0), "Test Failed: Wrong alert returned");
    }

    // Create an alert that resolves to HIGH severity
    @Test
    @DisplayName("Test Create High Severity Alert")
    void createHighSeverityAlertTest() {
        // Call createAlert with a latitude validation failure
        alertService.createAlert("TRK-101", "SRC-1", "Invalid latitude", "Track validation failure");

        // Capture the saved alert and verify severity is HIGH
        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(MTMSConstants.ALERT_HIGH, captor.getValue().getAlertSeverity(), "Test Failed: Expected HIGH severity");
    }

    // Create an alert that resolves to MEDIUM severity
    @Test
    @DisplayName("Test Create Medium Severity Alert")
    void createMediumSeverityAlertTest() {
        // Call createAlert with a stale source event
        alertService.createAlert("TRK-101", "SRC-1", "Source stale", "Source becomes stale");

        // Capture the saved alert and verify severity is MID
        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(MTMSConstants.ALERT_MID, captor.getValue().getAlertSeverity(), "Test Failed: Expected MID severity");
    }

    // Create an alert that resolves to LOW severity
    @Test
    @DisplayName("Test Create Low Severity Alert")
    void createLowSeverityAlertTest() {
        // Call createAlert with a duplicate message event
        alertService.createAlert("TRK-101", "SRC-1", "Repeated packet", "Duplicate message");

        // Capture the saved alert and verify severity is LOW
        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(MTMSConstants.ALERT_LOW, captor.getValue().getAlertSeverity(), "Test Failed: Expected LOW severity");
    }
}
