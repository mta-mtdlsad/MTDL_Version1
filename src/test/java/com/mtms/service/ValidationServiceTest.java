package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Track;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationService Class Test")
public class ValidationServiceTest {

    private ValidationService validationService;
    private Track validTrack;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        // Initialize service and a fully valid track
        validationService = new ValidationService();
        validTrack = new Track();
        validTrack.setSourceId("SRC_01");
        validTrack.setSequenceNumber(1);
        validTrack.setTrackNumber("TRK1001");
        validTrack.setLatitude(25.0);
        validTrack.setLongitude(55.0);
        validTrack.setSpeed(500.0);
        validTrack.setIdentityType(MTMSConstants.TRACK_FRIEND);
        validTrack.setTrackStatus(MTMSConstants.TRACK_ACTIVE);
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Return empty error list for a valid track
    @Test
    @DisplayName("Test Valid Track Returns No Errors")
    void validTrackTest() {
        // Call validation method
        List<String> errors = validationService.getValidationErrors(validTrack);

        // Check that error list is empty
        assertTrue(errors.isEmpty(), "Test Failed: Expected no validation errors");
    }

    /*// Return null payload error when track is null
    @Test
    @DisplayName("Test Null Track Returns Error")
    void nullTrackTest() {
        // Call validation with null
        List<String> errors = validationService.getValidationErrors(null);

        // Check returned error message
        assertEquals(1, errors.size());
        assertEquals("Track Payload Cannot Be Null Alert", errors.get(0));
    }

    // Return specific alerts when latitude is out of bounds and speed is zero
    @Test
    @DisplayName("Test Invalid Latitude And Speed")
    void invalidLatitudeAndSpeedTest() {
        // Set invalid latitude and zero speed
        validTrack.setLatitude(120.0);
        validTrack.setSpeed(0.0);

        // Call validation method
        List<String> errors = validationService.getValidationErrors(validTrack);

        // Check that both expected alerts are present
        assertTrue(errors.contains("latitude Alert"));
        assertTrue(errors.contains("speed Alert"));
    }*/
}
