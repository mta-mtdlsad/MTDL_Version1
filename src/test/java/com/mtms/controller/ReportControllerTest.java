package com.mtms.controller;

import com.mtms.main.MTMSApplication;
import com.mtms.service.TrackAnalyticsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MTMSApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Report Controller Class Test")
public class ReportControllerTest {

    @Autowired
    private ReportController reportController;

    @Autowired
    private TrackAnalyticsService trackAnalyticsService;

    public ReportControllerTest() {
        System.out.println("\nTest Instance is Created");
    }

    @BeforeAll
    public void beforeTestingStarts() {
        System.out.println("All Tests Start");
    }

    @BeforeEach
    public void init(TestInfo testInfo, TestReporter testReporter) {
        testReporter.publishEntry("Running: " + testInfo.getDisplayName());
        System.out.println("Report Controller Initialized");
    }

    // 1: TESTING CORRELATED TRACKS [getCorrelatedTracks()]
    @Test
    @DisplayName("Testing Correlated Tracks (Available or Empty)")
    @Tag("Report")
    // Testing to verify -> controller retrieves -> displays correlated tracks
    public void testGetCorrelatedTracks() {
        List<String> tracks = trackAnalyticsService.getCorrelatedTracks();
        String actual = reportController.getCorrelatedTracks();
        if (tracks == null || tracks.isEmpty()) {
            assertTrue(actual.contains("No tracks available."));
            //assertFalse(actual.contains("Tracks available."));  //Negative Testing
        } else {
            for (String track : tracks) {
                assertTrue(actual.contains(track));
                //assertFalse(actual.contains(track)); //Negative Testing
            }
        }
    }

    // 2: TESTING REPORT STATISTICS [showReportStat()]
    @Test
    @DisplayName("Testing Report Statistics Results")
    @Tag("Report")
    // Testing to verify -> controller retrieves -> displays all report statistics
    // report statistic covered -> Hostile Tracks + High Speed Tracks + Active Tracks + Inactive Tracks + Identity Count
    public void testShowReportStat() {
        List<String> tracks = trackAnalyticsService.showReportStat();
        String actual = reportController.showReportStat();
        assertNotNull(actual);
        for (String track : tracks) {
            assertTrue(actual.contains(track));
            //assertFalse(actual.contains(track)); //Negative Testing
        }
        //assertFalse(actual.contains("Incorrect Report Statistics")); //Negative Testing
    }

    @AfterEach
    public void postEachTest() {
        System.out.println("Test Concluded");
    }

    @AfterAll
    public void afterAllTestCases() {
        System.out.println("All Tests Concluded");
    }

}