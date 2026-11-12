package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.util.ValidationResult;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationServiceTest {

    private ValidationService validationService;

    private Track track;

    private ValidationResult validationResult;

    @BeforeEach
    public void init(TestInfo testInfo, TestReporter testReporter){
        // Publish the test info
        testReporter.publishEntry("\nRunning " + testInfo.getDisplayName());
        // New Track, ValidationService and ValidationResult for each test
        this.track = new Track();
        this.validationService = new ValidationService();
        this.validationResult = new ValidationResult();
    }

    // Runs after every test
    @AfterEach
    public void postEachTest(){
        System.out.println("Completed test");
    }

    // Test Source ID
    @Test
    @DisplayName("Testing Source ID Validation")
    public void validateSourceIdTest(){
        track.setSourceId("LINK-A");
        validationResult = ValidationService.validateSourceId(track);
        assertNull(validationResult, "Source ID Validation Failed");
    }

    //Test Invalid Source ID
    @Test
    @DisplayName("Testing Invalid Source ID Validation")
    public void validateSourceIdInvalidTest(){
        track.setSourceId("LINK@A");
        validationResult = ValidationService.validateSourceId(track);
        assertNull(validationResult, "Source ID Validation Failed");
    }

    // Test Sequence Number
    @Test
    @DisplayName("Testing Sequence Number Validation")
    public void validateSequenceNumberTest(){
        track.setSequenceNumber(10);
        validationResult = ValidationService.validateSequenceNumber(track);
        assertNull(validationResult, "Sequence Number Validation Failed");
    }

    //Test Invalid Sequence Number
    @Test
    @DisplayName("Testing Invalid Sequence Number Validation")
    public void validateSequenceNumberInvalidTest(){
        track.setSequenceNumber(-10);
        validationResult = ValidationService.validateSequenceNumber(track);
        assertNull(validationResult, "Sequence Number Validation Failed");
    }

    // Test Track Number
    @Test
    @DisplayName("Testing Track Number Validation")
    public void validateTrackNumberTest(){
        track.setTrackNumber("TRK001");
        validationResult = ValidationService.validateTrackNumber(track);
        assertNull(validationResult, "Track Number Validation Failed");
    }

    //Test Invalid Track Number
    @Test
    @DisplayName("Testing Invalid Track Number Validation")
    public void validateTrackNumberInvalidTest(){
        track.setTrackNumber("TRK");
        validationResult = ValidationService.validateTrackNumber(track);
        assertNull(validationResult, "Track Number Validation Failed");
    }

    //Test Invalid Null Track Number
    @Test
    @DisplayName("Testing Invalid Null Track Number Validation")
    public void validateTrackNumberInvalidNullTest(){
        track.setTrackNumber(null);
        validationResult = ValidationService.validateTrackNumber(track);
        assertNull(validationResult, "Track Number Validation Failed");
    }

    // Test Latitiude
    @Test
    @DisplayName("Testing Latitude Validation")
    public void validateLatitudeTest(){
        track.setLatitude(-80);
        validationResult = ValidationService.validateLatitude(track);
        assertNull(validationResult, "Track Latitude Validation Failed");
    }

    // Test Invalid Latitiude
    @Test
    @DisplayName("Testing Invalid Latitude Validation")
    public void validateLatitudeInvalidTest(){
        track.setLatitude(1000);
        validationResult = ValidationService.validateLatitude(track);
        assertNull(validationResult, "Track Latitude Validation Failed");
    }

    // Test Longitude
    @Test
    @DisplayName("Testing Longitude Validation")
    public void validateLongitudeTest(){
        track.setLatitude(170);
        validationResult = ValidationService.validateLongitude(track);
        assertNull(validationResult, "Track Longitude Validation Failed");
    }

    // Test Invalid Longitude
    @Test
    @DisplayName("Testing Invalid Longitude Validation")
    public void validateLongitudeInvalidTest(){
        track.setLongitude(1000);
        validationResult = ValidationService.validateLongitude(track);
        assertNull(validationResult, "Track Longitude Validation Failed");
    }

    // Test Speed
    @Test
    @DisplayName("Testing Speed Validation")
    public void validateSpeedTest(){
        track.setSpeed(500);
        validationResult = ValidationService.validateSpeed(track);
        assertNull(validationResult, "Track Speed Validation Failed");
    }

    // Test Invalid Speed
    @Test
    @DisplayName("Testing Invalid Speed Validation")
    public void validateSpeedInvalidTest(){
        track.setSpeed(3000);
        validationResult = ValidationService.validateSpeed(track);
        assertNull(validationResult, "Track Speed Validation Failed");
    }

    // Test Identity Type
    @Test
    @DisplayName("Testing Identity Type Validation")
    public void validateIdentityTypeTest(){
        track.setIdentityType("FRIEND");
        validationResult = ValidationService.validateIdentityType(track);
        assertNull(validationResult, "Track Identity Type Validation Failed");
    }

    // Test Invalid Identity Type
    @Test
    @DisplayName("Testing Invalid Identity Type Validation")
    public void validateIdentityTypeInvalidTest(){
        track.setIdentityType("TEST");
        validationResult = ValidationService.validateIdentityType(track);
        assertNull(validationResult, "Track Identity Type Validation Failed");
    }

    // Test Track Status
    @Test
    @DisplayName("Testing Track Status Validation")
    public void validateTrackStatusTest(){
        track.setTrackStatus("ACTIVE");
        validationResult = ValidationService.validateTrackStatus(track);
        assertNull(validationResult, "Track Status Validation Failed");
    }

    // Test Invalid Track Status
    @Test
    @DisplayName("Testing Invalid Track Status Validation")
    public void validateTrackStatusInvalidTest(){
        track.setTrackStatus("TEST");
        validationResult = ValidationService.validateTrackStatus(track);
        assertNull(validationResult, "Track Status Validation Failed");
    }

    // Test Created Time
    @Test
    @DisplayName("Testing Created Time Validation")
    public void validateCreatedTimeTest(){
        track.setCreatedTime(LocalDateTime.now());
        validationResult = ValidationService.validateCreatedTime(track);
        assertNull(validationResult, "Track Created Time Validation Failed");
    }

    // Test Invalid Created Time
    @Test
    @DisplayName("Testing Invalid Created Time Validation")
    public void validateCreatedTimeInvalidTest(){
        track.setCreatedTime(null);
        validationResult = ValidationService.validateCreatedTime(track);
        assertNull(validationResult, "Track Created Time Validation Failed");
    }

    // Test Track Validation
    @Test
    @DisplayName("Testing Track Validation")
    public void validateTrackTest(){
        track.setSourceId("LINK-4");
        track.setSequenceNumber(5);
        track.setTrackNumber("TRK1001");
        track.setLatitude(90);
        track.setLongitude(80);
        track.setSpeed(500);
        track.setTrackStatus("ACTIVE");
        track.setIdentityType("FRIEND");
        track.setCreatedTime(LocalDateTime.now());
        List<ValidationResult> errors = ValidationService.validateTrack(track);
        assertTrue(errors.isEmpty(),"Track Validation Failed");
    }

    // Test Invalid Track Validation
    @Test
    @DisplayName("Testing Invalid Track Validation")
    public void validateTrackInvalidTest(){
        track.setSourceId("LINK@4");
        track.setSequenceNumber(-1);
        track.setTrackNumber("TRK");
        track.setLatitude(-1000);
        track.setLongitude(1000);
        track.setSpeed(1000);
        track.setTrackStatus("TEST");
        track.setIdentityType("TEST");
        track.setCreatedTime(null);
        List<ValidationResult> errors = ValidationService.validateTrack(track);
        assertTrue(errors.isEmpty(), "Track Validation Failed");
    }
}
