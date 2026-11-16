package com.mtms.service;

import com.mtms.constants.MTMSConstants;
import com.mtms.domain.Alert;
import com.mtms.domain.Source;
import com.mtms.domain.Track;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.SourceRepository;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService Class Test")
public class AnalyticsServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

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

    // Retrieve alerts filtered by severity
    @Test
    @DisplayName("Test Get Alerts By Severity")
    void getAlertsBySeverityTest() {
        // Simulate repository returning one HIGH alert
        Alert alert = new Alert();
        when(alertRepository.selectSeverity("HIGH")).thenReturn(List.of(alert));

        // Call the service method
        List<Alert> result = analyticsService.getAlertsBySeverity("HIGH");

        // Check that one alert was returned
        assertEquals(1, result.size(), "Test Failed: Expected 1 high alert");
    }

    // Calculate overall source counters, track classifications, and high-speed tracks
    @Test
    @DisplayName("Test Get Overall Statistics")
    void getOverallStatisticsTest() {
        // Create sample source with packet counts
        Source source = new Source();
        source.setReceivedCount(10);
        source.setAcceptedCount(8);
        source.setRejectedCount(1);
        source.setDuplicateCount(1);

        // Create sample track with speed > 700.0
        Track track = new Track();
        track.setIdentityType(MTMSConstants.TRACK_FRIEND);
        track.setTrackStatus(MTMSConstants.TRACK_ACTIVE);
        track.setSpeed(800.0);

        // Simulate repository responses
        when(sourceRepository.findAll()).thenReturn(List.of(source));
        when(trackRepository.findAll()).thenReturn(List.of(track));

        // Call the service method
        Map<String, Object> stats = analyticsService.getOverallStatistics();

        // Check calculated totals and high-speed tracks list
        assertEquals(10, stats.get("totalProcessed"), "Test Failed: Total processed mismatch");
        assertEquals(8, stats.get("acceptedCount"), "Test Failed: Accepted count mismatch");
        List<?> highSpeed = (List<?>) stats.get("highSpeedTracks");
        assertEquals(1, highSpeed.size(), "Test Failed: High speed track not detected");
    }

    // Summarize alerts by total, severity, and event type
    @Test
    @DisplayName("Test Get Alert Summary")
    void getAlertSummaryTest() {
        // Create sample alert
        Alert alert = new Alert();
        alert.setAlertSeverity("HIGH");
        alert.setEventType("MALFORMED_MESSAGE");

        // Simulate repository returning the alert
        when(alertRepository.findAll()).thenReturn(List.of(alert));

        // Call the service method
        Map<String, Object> summary = analyticsService.getAlertSummary();

        // Check total alert count
        assertEquals(1, summary.get("totalAlerts"), "Test Failed: Total alerts mismatch");
    }
}
