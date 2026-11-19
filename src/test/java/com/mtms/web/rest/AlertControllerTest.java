package com.mtms.web.rest;

import com.mtms.domain.Alert;
import com.mtms.service.AlertService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // Retrieve all alerts via /api/alerts/list
    @Test
    @DisplayName("Test List Alerts")
    void listAlertsTest() {
        // Simulate service returning one alert
        Alert alert = new Alert();
        when(alertService.getAllAlerts()).thenReturn(List.of(alert));

        // Call controller method
        List<Alert> result = alertController.listAlerts();

        // Check returned list
        assertEquals(1, result.size(), "Test Failed: Incorrect number of alerts");
        assertEquals(alert, result.get(0));
    }
}
