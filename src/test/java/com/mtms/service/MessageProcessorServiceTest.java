package com.mtms.service;

import com.mtms.model.Track;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageProcessorService Class Test")
public class MessageProcessorServiceTest {

    @Mock
    private TrackService trackService;

    @Mock
    private AlertService alertService;

    @Mock
    private SourceHealthService sourceHealthService;

    @InjectMocks
    private MessageProcessorService messageProcessorService;

    private Track track;

    // Runs before each test
    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        // New alert for each test
        track = new Track("LINK-A", 1, "TRK1001", 10.0, 70.0, 550.0, "FRIEND", "ACTIVE", LocalDateTime.now());

        // Publish the test info
        testReporter.publishEntry("\nRunning " + testInfo.getDisplayName());
    }


    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Process a null track
    @Test
    @DisplayName("Test Null Track Processing")
    void nullTrackTest() {
        // Process a null track
        MessageProcessorService.ProcessingResult result = messageProcessorService.processDetailed(null);
        // Check that processing failed
        assertEquals(MessageProcessorService.Outcome.FAILURE, result.outcome(), "Test Failed: Expected FAILURE");
        // No services should be called
        verifyNoInteractions(trackService, alertService, sourceHealthService);
    }

    // Process a valid track
    @Test
    @DisplayName("Test Valid Track Processing")
    void validTrackTest() {
        // Simulate no previous sequence and successful save
        when(trackService.getHighestSequence("LINK-A")).thenReturn(0);
        when(trackService.saveTrack(track)).thenReturn(true);
        // Process the track
        MessageProcessorService.ProcessingResult result = messageProcessorService.processDetailed(track);
        // Check the result
        assertEquals(MessageProcessorService.Outcome.ACCEPTED, result.outcome(), "Test Failed: Track should be ACCEPTED");
        assertFalse(result.outOfOrder(), "Test Failed: Track should not be out-of-order");
        // Check that track was saved
        verify(trackService).saveTrack(track);
    }

    // Process a duplicate track
    @Test
    @DisplayName("Test Duplicate Track Processing")
    void duplicateTrackTest() {
        // Simulate duplicate track
        when(trackService.getHighestSequence("LINK-A")).thenReturn(100);
        when(trackService.saveTrack(track)).thenReturn(false);
        // Process the track
        MessageProcessorService.ProcessingResult result = messageProcessorService.processDetailed(track);
        // Check the result
        assertEquals(MessageProcessorService.Outcome.DUPLICATE, result.outcome(), "Test Failed: Expected DUPLICATE");
        // Check duplicate counter
        assertEquals(1, messageProcessorService.getDuplicateCount(), "Test Failed: Duplicate counter incorrect");
        // Check alert was created
        verify(alertService).createAlert(eq(track), any(com.mtms.util.ValidationResult.class));
    }

    // Process an out-of-order track
    @Test
    @DisplayName("Test Out Of Order Track Processing")
    void outOfOrderTrackTest() {
        // Simulate a higher previous sequence
        when(trackService.getHighestSequence("LINK-A")).thenReturn(200);
        when(trackService.saveTrack(track)).thenReturn(true);
        // Process the track
        MessageProcessorService.ProcessingResult result = messageProcessorService.processDetailed(track);
        // Check the result
        assertEquals(MessageProcessorService.Outcome.ACCEPTED, result.outcome(), "Test Failed: Track should be ACCEPTED");
        assertTrue(result.outOfOrder(), "Test Failed: Track should be out-of-order");
        // Check out-of-order counter
        assertEquals(1, messageProcessorService.getOutOfOrderCount(), "Test Failed: Out-of-order counter incorrect");
        // Check alert was created
        verify(alertService).createAlert(eq(track), any(com.mtms.util.ValidationResult.class));
    }

    // Check boolean process method
    @Test
    @DisplayName("Test Boolean Process Functionality")
    void processTest() {
        // Simulate successful save
        when(trackService.getHighestSequence("LINK-A")).thenReturn(0);
        when(trackService.saveTrack(track)).thenReturn(true);
        // Call process method
        boolean result = messageProcessorService.process(track);
        // Check that processing succeeded
        assertTrue(result, "Test Failed: Expected true for accepted track");

    }

    // Check processing counters
    @Test
    @DisplayName("Test Initial Processing Counters")
    void initialCountersTest() {
        // Check that counters start at zero
        assertEquals(0, messageProcessorService.getDuplicateCount(), "Test Failed: Duplicate counter should start at zero");
        assertEquals(0, messageProcessorService.getOutOfOrderCount(), "Test Failed: Out-of-order counter should start at zero");
    }



    // Check failed track saving
    @Test
    @DisplayName("Test Track Processing Exception")
    void processingExceptionTest() {
        // Simulate an exception when finding the sequence
        when(trackService.getHighestSequence("LINK-A")).thenThrow(new RuntimeException("Database error"));
        // Process the track
        MessageProcessorService.ProcessingResult result = messageProcessorService.processDetailed(track);
        // Check that processing failed
        assertEquals(MessageProcessorService.Outcome.FAILURE, result.outcome(), "Test Failed: Expected FAILURE after exception");
    }
}