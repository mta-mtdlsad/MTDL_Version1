package com.mtms.controller;

import com.mtms.main.MTMSApplication;
import com.mtms.repository.AlertRepository;
import com.mtms.service.AlertService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MTMSApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Alert Controller Class Test")
public class AlertControllerTest {

    @Autowired
    private AlertController alertController;

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertRepository alertRepository;

    public AlertControllerTest() {
        System.out.println("\nTest Instance is Created");
    }

    @BeforeAll
    public void beforeTestingStarts() {
        System.out.println("All Tests Start");
    }

    @BeforeEach
    public void init(TestInfo testInfo, TestReporter testReporter) {
        testReporter.publishEntry("Running: " + testInfo.getDisplayName());
        alertRepository.deleteAll(); // Ensures every test starts with an empty alert table
        System.out.println("Alert Controller Initialized");
    }


    // 1: TESTING RETRIEVING EXISTING ALERTS
    @Nested
    @DisplayName("Testing Get All Alerts Functionality")
    @Tag("Alert")
    class GetAllAlertsTest {

        // Test 1.1
        @Test
        @DisplayName("Testing with Existing Alerts [OneAlerts]")
        //Testing to verify controller -> retrieves existing alerts from PostgreSQL
        public void testGetAllAlerts() {
            alertService.createAlertObject("TRK001", "Alert Message 1", "HIGH", "LINK-A", "Malformed message");
            String actual = alertController.getAllAlerts();
            assertTrue(actual.contains("TRK001"));
            //assertTrue(actual.contains("TRK999"));            //Negative Testing
            assertTrue(actual.contains("Alert Message 1"));
            assertTrue(actual.contains("HIGH"));
        }

        // Test 1.2
        @Test
        @DisplayName("Testing with Multiple Alerts [ListOfAlerts]")
        //Testing to verify controller -> retrieves multiple alerts from PostgreSQL
        public void testGetMultipleAlerts() {
            alertService.createAlertObject("TRK001", "Alert Message 1", "HIGH", "LINK-A", "Malformed message");
            alertService.createAlertObject("TRK002", "Alert Message 2", "LOW", "LINK-B", "Duplicate message");
            alertService.createAlertObject("TRK003", "Alert Message 3", "MEDIUM", "LINK-C", "Track validation failure");
            String actual = alertController.getAllAlerts();
            assertAll(
                    () -> assertTrue(actual.contains("TRK001")),
                    () -> assertTrue(actual.contains("TRK002")),
                    () -> assertTrue(actual.contains("TRK003")),
                    //() -> assertTrue(actual.contains("TRK444")),            //Negative Testing
                    () -> assertTrue(actual.contains("Alert Message 1")),
                    () -> assertTrue(actual.contains("Alert Message 2")),
                    () -> assertTrue(actual.contains("Alert Message 3")));
        }

        // Test 1.3
        @Test
        @DisplayName("Testing All Alert Attributes in Response")
        //Testing to verify controller -> returns all alert attributes in its response
        public void testAlertDetails() {
            alertService.createAlertObject("TRK001", "Test alert", "HIGH", "LINK-A", "Malformed message");
            String actual = alertController.getAllAlerts();
            assertAll(
                    () -> assertTrue(actual.contains("Alert ID:")),
                    () -> assertTrue(actual.contains("Track Number: TRK001")),
                    () -> assertTrue(actual.contains("Source Id: LINK-A")),
                    //() -> assertTrue(actual.contains("Source Id: LINK-B")),            //Negative Testing
                    () -> assertTrue(actual.contains("Alert Message: Test alert")),
                    () -> assertTrue(actual.contains("Alert Severity: HIGH")),
                    () -> assertTrue(actual.contains("Event Type: Malformed message")));
        }

        // Test 1.4
        @Test
        @DisplayName("Testing with No Alerts Exist [EmptyListOfAlerts]")
        //Testing to verify controller -> returns -> correct message when no alerts exist in PostgreSQL
        public void testGetAllAlertsEmpty() {
            String actual = alertController.getAllAlerts();
            assertEquals("Alerts could not be found.", actual);
            //assertEquals("No alerts found.", actual);            //Negative Testing
        }

    }

    @AfterEach
    public void postEachTest() {
        alertRepository.deleteAll();
        System.out.println("Test Concluded");
    }

    @AfterAll
    public void afterAllTestCases() {
        System.out.println("All Tests Concluded");
    }

}