package com.mtms.service;

import com.mtms.domain.Alert;
import com.mtms.main.MTMSApplication;
import com.mtms.repository.AlertRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;

@SpringBootTest(classes = MTMSApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Alert Service Class Test")
public class AlertServiceTest {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertService alertService;

    public AlertServiceTest() {
        System.out.println("\nTest Instance is Created");
    }

    @BeforeAll
    public void beforeTestingStarts() {
        System.out.println("All Tests Start");
    }

    @BeforeEach
    public void init(TestInfo testInfo, TestReporter testReporter) {
        testReporter.publishEntry("Running: " + testInfo.getDisplayName());
        alertRepository.deleteAll(); //Ensures that every test starts with an empty alert table
        System.out.println("Alert Service Initialized");
    }

    // 1: TESTING PROCESS ALERT [processAlert()]
    @Nested
    @DisplayName("Testing Process Alert Functionality")
    @Tag("Alert")
    class ProcessAlertTest {

        //Test1.1.1:
        @Test
        @DisplayName("Testing Malformed Message")
        //Testing to verify -> system receives a malformed message-> creates an alert -> saves it in PostgreSQL -> assigns it HIGH severity
        public void testMalformedMessage() {
            String actual = alertService.processAlert("LINK-A", "TRK1001", "Malformed message received", "Malformed message");
            assertEquals("Success", actual); //Compares two values [if match-> pass, if not-> fail]
            List<Alert> alerts = alertService.getAllAlerts(); //Retrieving all alerts
            assertEquals(1, alerts.size()); //Number of alerts [if database has 1-> pass, otherwise fail]
            assertEquals("HIGH", alerts.get(0).getAlertSeverity()); //If severity is HIGH -> pass, otherwise fail
            //assertEquals("LOW", alerts.get(0).getAlertSeverity()); //Negative Testing
        }

        //Test1.1.2:
        @Test
        @DisplayName("Testing Short Track Number")
        //Testing to verify -> system receives a short track number -> modifies that number by adding INVALID() -> saving the alert in PostgreSQL
        public void testShortTrackNumber() {
            String actual = alertService.processAlert("LINK-B", "123", "short track number", "Track validation failure");
            assertEquals("Success", actual); //Compares two values [if match-> pass, if not-> fail]
            List<Alert> alerts = alertService.getAllAlerts(); //Retrieving all alerts
            assertEquals(1, alerts.size()); //Number of alerts [if database has 1-> pass, otherwise fail]
            assertEquals("INVALID(123)", alerts.get(0).getTrackNumber()); //If actual track number is INVALID(123) -> pass, otherwise fail
            //assertEquals("INVALID(TRK002)", alerts.get(0).getTrackNumber()); //Negative Testing
        }

        //Test1.1.3:
        @Test
        @DisplayName("Testing Duplicate Message")
        //Testing to verify -> system receives a duplicate message -> creates an alert -> assigns it LOW severity
        public void testDuplicateMessage() {
            String actual = alertService.processAlert("LINK-C", "TRK002", "Duplicate message received", "Duplicate message");
            assertEquals("Success", actual); //Compares two values [if match-> pass, if not-> fail]
            List<Alert> alerts = alertService.getAllAlerts(); //Retrieving all alerts
            assertEquals(1, alerts.size()); //Number of alerts [if database has 1-> pass, otherwise fail]
            assertEquals("LOW", alerts.get(0).getAlertSeverity()); //If severity is LOW -> pass, otherwise fail
            //assertEquals("HIGH", alerts.get(0).getAlertSeverity()); //Negative Testing
        }

        // 1.2: TESTING SEVERITY ASSIGNMENT [chooseSeverity()]:
        @Test
        @DisplayName("Testing Severity Assignment")
        //Testing to verify -> system assigns the correct severity to each type of track event
        public void testSeverityAssignment() {

            alertService.processAlert("LINK-A", "TRK001", "Malformed message", "Malformed message");
            alertService.processAlert("LINK-A", "TRK002", "Disconnected", "Source becomes disconnected");
            alertService.processAlert("LINK-A", "TRK003", "Validation failure", "Track validation failure");
            alertService.processAlert("LINK-A", "TRK004", "Stale source", "Source becomes stale");
            alertService.processAlert("LINK-A", "TRK005", "Duplicate message", "Duplicate message");
            alertService.processAlert("LINK-A", "TRK006", "Out of order", "Out-of-order message");
            List<Alert> alerts = alertService.getAllAlerts(); //Retrieving all alerts
            assertEquals(6, alerts.size()); //Number of alerts [if database has 6-> pass, otherwise fail]
            alerts.sort(Comparator.comparing(Alert::getTrackNumber)); //Sorts alerts by track number in ascending order
            // to verify -> each track number receives the correct alert severity
            assertAll(
                    () -> assertEquals("TRK001", alerts.get(0).getTrackNumber()),
                    () -> assertEquals("HIGH", alerts.get(0).getAlertSeverity()),

                    () -> assertEquals("TRK002", alerts.get(1).getTrackNumber()),
                    () -> assertEquals("HIGH", alerts.get(1).getAlertSeverity()),

                    () -> assertEquals("TRK003", alerts.get(2).getTrackNumber()),
                    () -> assertEquals("MEDIUM", alerts.get(2).getAlertSeverity()),

                    () -> assertEquals("TRK004", alerts.get(3).getTrackNumber()),
                    () -> assertEquals("MEDIUM", alerts.get(3).getAlertSeverity()),

                    () -> assertEquals("TRK005", alerts.get(4).getTrackNumber()),
                    () -> assertEquals("LOW", alerts.get(4).getAlertSeverity()),

                    () -> assertEquals("TRK006", alerts.get(5).getTrackNumber()),
                    () -> assertEquals("LOW", alerts.get(5).getAlertSeverity()));
            //() -> assertEquals("HIGH", alerts.get(5).getAlertSeverity())); //Negative Testing
        }
    }

    // 2: TESTING CREATE ALERT OBJECT [createAlertObject()]
    //Testing to verify -> createAlertObject() -> creates an Alert object -> saves it in PostgreSQL -> stores all attributes
    @Nested
    @DisplayName("Testing Create Alert Functionality")
    @Tag("Alert")
    class CreateAlertTest {

        @Test
        @DisplayName("Testing Alert Is Successfully Saved")
        public void testCreateAlertSuccess() {
            String actual = alertService.createAlertObject("TRK001", "Test alert", "HIGH", "LINK-A", "Malformed message");
            assertEquals("Success", actual); //Compares two values [if match-> pass, if not-> fail]
            List<Alert> alerts = alertService.getAllAlerts(); //Retrieving all alerts
            assertEquals(1, alerts.size()); //Number of alerts [if database has 1-> pass, otherwise fail]
            assertAll(
                    () -> assertEquals("TRK001", alerts.get(0).getTrackNumber()), //If first object from TrackNumber is TRK001 -> pass, otherwise fail
                    () -> assertEquals("Test alert", alerts.get(0).getAlertMessage()), //If first object from AlertMessage is Test alert -> pass, otherwise fail
                    () -> assertEquals("HIGH", alerts.get(0).getAlertSeverity()), //If first object from AlertSeverity is HIGH -> pass, otherwise fail
                    //() -> assertEquals("LOW", alerts.get(0).getAlertSeverity()), //Negative Testing
                    () -> assertEquals("LINK-A", alerts.get(0).getSourceId()), //If first object from SourceId is LINK-A -> pass, otherwise fail
                    () -> assertEquals("Malformed message", alerts.get(0).getEventType()), //If first object from EventType is Malformed message -> pass, otherwise fail
                    () -> assertNotNull(alerts.get(0).getAlertTime())); //check first alert has (date,time) assigned -> pass, otherwise fail
        }
    }

    // 3: TESTING GET ALL ALERTS [getAllAlerts()]
    @Nested
    @DisplayName("Testing Get All Alerts Functionality")
    @Tag("Alert")
    class GetAllAlertsTest {

        //Test3.1:
        @Test
        @DisplayName("Testing with Retrieving Existing Alerts")
        //Testing to verify -> getAllAlerts() ->  retrieves all existing alerts from PostgreSQL
        public void testGetAllAlerts() {
            alertService.createAlertObject("TRK001", "Alert Message 1", "HIGH", "LINK-A", "Malformed message");
            alertService.createAlertObject("TRK002", "Alert Message 2", "LOW", "LINK-B", "Duplicate message");
            alertService.createAlertObject("TRK003", "Alert Message 3", "HIGH", "LINK-C", "Track validation failure");
            List<Alert> alerts = alertService.getAllAlerts(); //Retrieving all alerts
            assertEquals(3, alerts.size()); //Number of alerts [if database has 3-> pass, otherwise fail]
            //assertEquals(2, alerts.size()); //Negative Testing

        }

        //Test3.2:
        @Test
        @DisplayName("Testing with No Alerts Exist")
        //Testing to verify -> getAllAlerts() returns an empty list when PostgreSQL contains no alerts
        public void testGetAllAlertsEmpty() {
            List<Alert> alerts = alertService.getAllAlerts();
            assertTrue(alerts.isEmpty()); //If the alerts list is empty -> pass, otherwise fail
            //assertFalse(alerts.isEmpty()); //Negative Testing
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

