package com.mtms.service;

import com.mtms.domain.Alert;
import com.mtms.domain.Source;
import com.mtms.domain.Track;
import com.mtms.main.MTMSApplication;
import com.mtms.repository.AlertRepository;
import com.mtms.repository.SourceRepository;
import com.mtms.service.SourceService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = MTMSApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional

public class SourceServiceTest {

    @Autowired
    private SourceService sourceService;

    @Autowired
    private SourceRepository sourceRepository;
    @Autowired
    private AlertRepository alertRepository;


    @BeforeAll
    public void beforeAllTests(){
        System.out.print("All Tests Start");
    }

    @BeforeEach
    public void init(TestInfo testInfo, TestReporter Tr) {
        Tr.publishEntry("Running: " + testInfo.getDisplayName());
        sourceRepository.deleteAll();
    }


    //processSources - Negative (Null Track Data)
    @Test
    @Tag("Source")
    @DisplayName("Testing Process Source Empty List")
    public void testNullProcessSources() {

        //create empty list and call method
        Track track = null;
        sourceService.processSources(track);

        //checks if track is null
        assertNull(track, "Expected track to be null");
    }


    //processSources - Positive (Valid Track)
    @Test
    @Tag("Source")
    @DisplayName("Testing Process Sources With Valid Track")
    public void testInvalidSourceProcessSources(TestReporter Tr) {

        //create new source and save it in DB
        String sourceId = "LINK-C";
        Source source = new Source();
        source.setSourceId(sourceId);
        source.setLatestSequence(0);
        source.setSourceStatus("INACTIVE");
        sourceRepository.save(source);

        //create new track
        Track track = new Track(
                "TRK1001", 25.2769, 55.2962, 100.0,
                "TEST", "ACTIVE", sourceId, 1,
                LocalDateTime.parse("2026-11-13T09:59:50"),
                LocalDateTime.parse("2026-11-13T10:00:00")
        );

        //call method
        sourceService.processSources(track);

        //retrieve updated source from repository
        Source result = sourceRepository.findBySourceId(sourceId);

        //check track is not null
        assertNotNull(track, "Expected track to be created");
        assertEquals(1, result.getLatestSequence(), "Expected sequence number to be updated");
        assertEquals("ACTIVE", result.getSourceStatus(), "Expected source status to be ACTIVE");
        Tr.publishEntry("Source Information: " + result);

    }


    //findSourceById - Positive/Negative (Valid and Invalid Source ID)
    @Test
    @Tag("Source")
    @DisplayName("Testing Get Sources By Valid/Invalid Source ID")
    public void testFindSourceById() {
        //create 2 valid  sources (will be saved to the database) and 1 invalid source (that will not exist in database)
        String source1IdValid = "LINK-A";
        String source2IdValid = "LINK-B";
        String sourceIdInvalid = "linka";

        //set valid source id and parameters, and save to source repository

        Source source1 = new Source();
        source1.setSourceId(source1IdValid);
        source1.setFirstSeenAt(LocalDateTime.parse("2026-11-02T08:30:00"));
        source1.setLastReceivedAt(LocalDateTime.parse("2026-11-02T10:30:00"));
        source1.setLatestSequence(5);
        source1.setSourceStatus("INACTIVE");
        sourceRepository.save(source1);

        Source source2 = new Source();
        source2.setSourceId(source2IdValid);
        source2.setFirstSeenAt(LocalDateTime.parse("2026-10-02T09:30:00"));
        source2.setLastReceivedAt(LocalDateTime.parse("2026-10-02T10:30:00"));
        source2.setLatestSequence(18);
        source2.setSourceStatus("ACTIVE");
        sourceRepository.save(source2);

        //call method on both valid and invalid inputs
        List<Source> valid1Result = sourceService.findSourceById(source1IdValid);
        List<Source> valid2Result = sourceService.findSourceById(source2IdValid);
        List<Source> invalidResult = sourceService.findSourceById(sourceIdInvalid);

        //check both lists are not null, and validResult contains matching source id, and invalidResult is empty
        assertAll(
                () -> assertNotNull(valid1Result, "ValidResult list should not be null"),
                () -> assertFalse(valid1Result.isEmpty(), "Expected valid source to be found"),
                () -> assertEquals(source1IdValid, valid1Result.get(0).getSourceId(), "Valid source ID should match"),
                () -> assertNotNull(valid2Result, "ValidResult list should not be null"),
                () -> assertFalse(valid2Result.isEmpty(), "Expected valid source to be found"),
                () -> assertEquals(source2IdValid, valid2Result.get(0).getSourceId(), "Valid source ID should match"),
                () -> assertNotNull(invalidResult, "InvalidResult list should not be null"),
                () -> assertTrue(invalidResult.isEmpty(), "Expected empty list when source ID doesnt exist")
        );
    }

    //findAllSources - Positive (Sources in DB)
    @Test
    @Tag("Source")
    @DisplayName("Testing Get All Sources")
    public void positiveTestFindAllSources(TestReporter Tr) {

        //create 3 new sources
        Source source1 = new Source();
        Source source2 = new Source();
        Source source3 = new Source();

        //set source id for newly created sources
        source1.setSourceId("LINK-F");
        source2.setSourceId("LINK-G");
        source3.setSourceId("LINK-M");

        //save created sources into source repository
        sourceRepository.save(source1);
        sourceRepository.save(source2);
        sourceRepository.save(source3);

        //call method
        List<Source> result = sourceService.findAllSources();

        //check list is not null, and is not empty, then prints total size of sources retrieved

        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected the list of sources to not be empty");
        Tr.publishEntry("Total sources found: " + result.size());
    }

    //findAllSources - Negative (Empty List)
    @Test
    @Tag("Source")
    @DisplayName("Testing Get All Sources Empty List")
    public void negativeTestFindAllSources(TestReporter Tr) {

        //create empty list and call method
        List<Source> result = sourceService.findAllSources();

        //checks list is not null, and is empty, then prints message in case of error
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected the list of sources to be empty");

        //print total size of sources retrieved (should equal to zero)
        Tr.publishEntry("Total sources found: " + result.size());
    }

    //registerSources - Positive (New Sources)
    @Test
    @Tag("Source")
    @DisplayName("Testing Register New Sources")
    public void positiveRegisterSource(TestReporter Tr) {
        //create new source and call method
        String sourceId = "LINK-X";
        sourceService.registerSource(sourceId);

        //retrieve source from the repository
        Source result = sourceRepository.findBySourceId(sourceId);

        //check result is not null, and that source Id matches
        assertNotNull(result);
        assertEquals(sourceId, result.getSourceId(), "Source Id should match");

        //print success message
        Tr.publishEntry("New source registered successfully.");
    }

    //registerSources - Negative (Existing Duplicate Sources)
    @Test
    @Tag("Source")
    @DisplayName("Testing Register Existing Sources")
    public void negativeRegisterSource() {

        //create new source to exist in database
        Source source = new Source();
        source.setSourceId("LINK-B");
        source.setFirstSeenAt(LocalDateTime.parse("2026-11-02T08:30:00"));
        source.setLastReceivedAt(LocalDateTime.parse("2026-11-02T10:30:00"));
        source.setLatestSequence(5);
        source.setSourceStatus("INACTIVE");
        sourceRepository.save(source);

        //attempt to create source again (it already exists in DB)
        String sourceId = "LINK-B";
        sourceService.registerSource(sourceId);

        //retrieve source from the repository
        Source result = sourceRepository.findBySourceId(sourceId);

        //check result is not null, and that source Id matches
        assertNotNull(result);
        assertEquals(sourceId, result.getSourceId(), "Source Id should match");

    }

    //recieveMessage - Positive (Registered Source)
    @Test
    @Tag("Source")
    @DisplayName("Testing Receive Message From Existing Source")
    public void positiveTestReceiveMessage(TestReporter Tr) {

        //create new source and save it in DB
        String sourceId = "LINK-C";
        Source source = new Source();
        source.setSourceId(sourceId);
        source.setFirstSeenAt(LocalDateTime.parse("2026-11-02T08:30:00"));
        source.setLastReceivedAt(LocalDateTime.parse("2026-11-02T10:30:00"));
        source.setLatestSequence(0);
        source.setSourceStatus("INACTIVE");
        sourceRepository.save(source);

        //call the recieveMessage method with a valid sequence number
        boolean result = sourceService.receiveMessage(sourceId,5);
        Source updatedSource = sourceRepository.findBySourceId(sourceId);

        //check source exists and message is received, and sequenceNumber/sourceStatus/lastRecievedTime is updated
        assertAll(
                () -> assertTrue(result,"Message should be successfully received"),
                () -> assertEquals(5, updatedSource.getLatestSequence(), "Sequence number should be updated"),
                () -> assertNotEquals(LocalDateTime.parse("2026-11-02T10:30:00"), updatedSource.getLastReceivedAt(),"Last received time should be updated"),
                () -> assertEquals("ACTIVE", updatedSource.getSourceStatus(), "Source status should be ACTIVE")
        );

        //print message
        Tr.publishEntry("Source Information: " + updatedSource);

    }

    //recieveMessage - Positive (New Source)
    @Test
    @Tag("Source")
    @DisplayName("Testing Receive Message From New Source")
    public void positiveTestReceiveMessageNewSource(TestReporter Tr) {

        //create source id that does not exist
        String sourceId = "LINK-T";

        //call the recieveMessage method with a valid sequence number
        boolean result = sourceService.receiveMessage(sourceId, 6);

        //retrieve automatically registered source from repository
        Source updatedSource = sourceRepository.findBySourceId(sourceId);

        //check source was registered and message was received, and source status and last recieved is updated
        assertAll(
                () -> assertTrue(result, "Expected message to be received successfully"),
                () ->  assertNotNull(updatedSource, "Expected new source to be registered"),
                () ->  assertEquals(6, updatedSource.getLatestSequence(),"Sequence number should be updated"),
                () -> assertNotNull(updatedSource.getLastReceivedAt(), "Last received time should be updated"),
                () ->  assertEquals("ACTIVE", updatedSource.getSourceStatus())
        );

        //print message
        Tr.publishEntry("Source Information: " + updatedSource);
    }

    //recieveMessage - Negative (Invalid Sequence Number)
    @Test
    @Tag("Source")
    @DisplayName("Testing Receive Message with Invalid Sequence Number")
    public void negativeTestReceiveMessage(TestReporter Tr) {

        //create new source and save it in DB
        String sourceId = "LINK-F";
        Source source = new Source();
        source.setSourceId(sourceId);
        source.setFirstSeenAt(LocalDateTime.parse("2026-11-02T08:30:00"));
        source.setLastReceivedAt(LocalDateTime.parse("2026-11-02T10:30:00"));
        source.setLatestSequence(0);
        source.setSourceStatus("INACTIVE");
        sourceRepository.save(source);

        //call the recieveMessage method with an Invalid sequence number
        boolean result = sourceService.receiveMessage(sourceId,0);
        Source updatedSource = sourceRepository.findBySourceId(sourceId);

        //check message was rejected due to invalid sequence number
        assertFalse(result,"Expected message with invalid sequence number to be rejected");

    }

    //calculateSourceStatus - Positive (All Source Statuses)
    @Test
    @Tag("Source")
    @DisplayName("Testing Calculate Source Status")
    public void testCalculateSourceStatus(TestReporter Tr)
    {
        LocalDateTime checkTime = LocalDateTime.parse("2026-11-13T10:00:00");

        //create new source and save it in DB (NOT SEEN)
        String sourceId1 = "LINK-O";
        Source source1 = new Source();
        source1.setSourceId(sourceId1);
        source1.setLastReceivedAt(null);
        source1.setSourceStatus("ACTIVE");

        //call method and retrieve source from repository
        String result1 = sourceService.calculateSourceStatus(source1, checkTime);

        //check if status equals to NOT SEEN
        assertEquals("NOT SEEN", result1, "Expected NOT SEEN but Received Different Status");


        //create new source and save it in DB (15 Seconds since Last Received = ACTIVE)
        String sourceId2 = "LINK-L";
        Source source2 = new Source();
        source2.setSourceId(sourceId2);
        source2.setLastReceivedAt(LocalDateTime.parse("2026-11-13T09:59:45"));
        source2.setSourceStatus("ACTIVE");

        //call method and retrieve source from repository
        String result2 = sourceService.calculateSourceStatus(source2, checkTime);

        //check if status equals to ACTIVE
        assertEquals("ACTIVE", result2, "Expected ACTIVE but Received Different Status");


        //create new source and save it in DB (15 to 60 Seconds since Last Received = STALE)
        String sourceId3 = "LINK-P";
        Source source3 = new Source();
        source3.setSourceId(sourceId3);
        source3.setLastReceivedAt(LocalDateTime.parse("2026-11-13T09:59:40"));
        source3.setSourceStatus("ACTIVE");

        //call method and retrieve source from repository
        String result3 = sourceService.calculateSourceStatus(source3, checkTime);

        //check if status equals to STALE
        assertEquals("STALE", result3, "Expected STALE but Received Different Status");


        //create new source and save it in DB (more than 60 Seconds since Last Received = DISCONNECTED)
        String sourceId4 = "LINK-Q";
        Source source4 = new Source();
        source4.setSourceId(sourceId4);
        source4.setLastReceivedAt(LocalDateTime.parse("2026-11-13T09:58:59"));
        source4.setSourceStatus("ACTIVE");

        //call method and retrieve source from repository
        String result4 = sourceService.calculateSourceStatus(source4, checkTime);

        //check if status equals to DISCONNECTED
        assertEquals("DISCONNECTED", result4, "Expected DISCONNECTED but Received Different Status");

        Tr.publishEntry("Successfully tested all 4 source statuses.");
    }


    //calculateSourceHealth - Negative (Empty List)
    @Test
    @Tag("Source")
    @DisplayName("Testing Calculate Source Health Empty List")
    public void negativeTestCheckSourceHealth(TestReporter Tr) {

        //call method
        sourceService.checkSourceHealth("TRK1001");

        //create new list and retrieve sources from repository
        List<Source> sources = new ArrayList<>();
        sourceRepository.findAll().forEach(sources::add);

        //check list is not null, and is empty
        assertNotNull(sources);
        assertTrue(sources.isEmpty(), "Expected the list of sources to be empty");
        Tr.publishEntry("Total sources found: " + sources.size());
    }


    //calculateSourceHealth - Positive (ACTIVE>STALE)
    @Test
    @Tag("Source")
    @DisplayName("Testing Check Source Health With ACTIVE")
    public void testActiveCheckSourceHealth(TestReporter Tr)
    {
        //create new source and save it in DB
        String sourceId = "LINK-S";
        Source source = new Source();
        source.setSourceId(sourceId);
        source.setLastReceivedAt(LocalDateTime.parse("2026-11-13T09:59:40"));
        source.setSourceStatus("ACTIVE");
        sourceRepository.save(source);

        //call method and retrieve source from repository
        LocalDateTime checkTime = LocalDateTime.parse("2026-11-13T10:00:00");
        sourceService.checkSourceHealth("TRK1001", checkTime);
        Source result = sourceRepository.findBySourceId(sourceId);

        //retrieve alerts from alert repository
        List<Alert> alerts = new ArrayList<>();
        alertRepository.findAll().forEach(alerts::add);

        //check if status equals to DISCONNECTED and if an alert  is created
        assertEquals("STALE", result.getSourceStatus(), "Expected STALE but Received Different Status");
        assertEquals(1, alerts.size(), "Expected 1 alert to be created");
        assertEquals("MEDIUM", alerts.get(0).getAlertSeverity(), "Expected MEDIUM alert severity");
        Tr.publishEntry("Alerts created: " + alerts.size());


    }

    //calculateSourceHealth - Positive (ACTIVE>DISCONNECTED)
    @Test
    @Tag("Source")
    @DisplayName("Testing Check Source Health With DISCONNECTED")
    public void testDisconnectedCheckSourceHealth(TestReporter Tr)
    {
        //create new source and save it in DB
        String sourceId = "LINK-D";
        Source source = new Source();
        source.setSourceId(sourceId);
        source.setLastReceivedAt(LocalDateTime.parse("2026-11-13T09:58:59"));
        source.setSourceStatus("ACTIVE");
        sourceRepository.save(source);

        //call method and retrieve source from repository
        LocalDateTime checkTime = LocalDateTime.parse("2026-11-13T10:00:00");
        sourceService.checkSourceHealth("TRK1001", checkTime);
        Source result = sourceRepository.findBySourceId(sourceId);

        //retrieve alerts from alert repository
        List<Alert> alerts = new ArrayList<>();
        alertRepository.findAll().forEach(alerts::add);

        //check if status equals to DISCONNECTED
        assertEquals("DISCONNECTED", result.getSourceStatus(), "Expected DISCONNECTED but Received Different Status");
        assertEquals(1, alerts.size(), "Expected 1 alert to be created");
        assertEquals("HIGH", alerts.get(0).getAlertSeverity(), "Expected HIGH alert severity");
        Tr.publishEntry("Alerts created: " + alerts.size());

    }

    @AfterEach
    public void postEachTest() {
        sourceRepository.deleteAll();
        System.out.println("Test Concluded");
    }

    @AfterAll
    public void afterAllTests() {
        System.out.println("All tests Concluded");

    }
}






