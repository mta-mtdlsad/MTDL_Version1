package com.mtms.web.rest;

import com.mtms.constants.MTMSConstants;
import com.mtms.domain.Track;
import com.mtms.service.AnalyticsService;
import com.mtms.service.TrackService;
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
@DisplayName("ReportController Class Test")
public class ReportControllerTest {

    @Mock
    private TrackService trackService;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private ReportController reportController;

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

    // Calculate track counts by identity and status
    @Test
    @DisplayName("Test Get Statistics")
    void getStatisticsTest() {
        // Create sample FRIEND and ACTIVE track
        Track t1 = new Track();
        t1.setIdentityType(MTMSConstants.TRACK_FRIEND);
        t1.setTrackStatus(MTMSConstants.TRACK_ACTIVE);

        // Simulate service returning the track
        when(trackService.getAllTracks()).thenReturn(List.of(t1));

        // Call controller method
        Map<String, Object> stats = reportController.getStatistics();

        // Check computed counts
        assertEquals(1, stats.get("totalTracks"));
        assertEquals(1, stats.get("friendCount"));
        assertEquals(1, stats.get("activeCount"));
    }

    // Retrieve alert summary report
    @Test
    @DisplayName("Test Get Alert Summary Report")
    void getAlertSummaryReportTest() {
        // Simulate analytics service response
        Map<String, Object> mockSummary = Map.of("totalAlerts", 5);
        when(analyticsService.getAlertSummary()).thenReturn(mockSummary);

        // Call controller method
        Map<String, Object> result = reportController.getAlertSummaryReport();

        // Check total alerts value
        assertEquals(5, result.get("totalAlerts"));
    }

    // Retrieve latest tracks view
    @Test
    @DisplayName("Test Get Latest Tracks")
    void getLatestTracksTest() {
        // Simulate service returning one latest track
        when(trackService.getLatestTracksView()).thenReturn(List.of(new Track()));

        // Call controller method
        List<Track> result = reportController.getLatestTracks();

        // Check returned list size
        assertEquals(1, result.size());
    }
}
