package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BatchTrackService Class Test")
public class BatchTrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ValidationService validationService;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private BatchTrackService batchTrackService;

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

    // Save all valid tracks in a batch without creating alerts
    @Test
    @DisplayName("Test Process Valid Batch")
    void processValidBatchTest() {
        // Simulate zero validation errors for valid track
        Track validTrack = new Track();
        when(validationService.getValidationErrors(validTrack)).thenReturn(List.of());

        // Call batch processing
        batchTrackService.processTrackBatch(List.of(validTrack));

        // Verify track was saved and no alerts were created
        verify(trackRepository).saveAll(List.of(validTrack));
        verifyNoInteractions(alertService);
    }

    // Log alerts for invalid tracks and skip batch save
    @Test
    @DisplayName("Test Process Invalid Batch")
    void processInvalidBatchTest() {
        // Create an invalid track
        Track invalidTrack = new Track();
        invalidTrack.setTrackNumber("TRK-1");
        invalidTrack.setSourceId("SRC-1");

        // Simulate returning a validation error
        when(validationService.getValidationErrors(invalidTrack)).thenReturn(List.of("speed Alert"));

        // Call batch processing
        batchTrackService.processTrackBatch(List.of(invalidTrack));

        // Verify alert was created and saveAll was never called
        verify(alertService).createAlert("TRK-1", "SRC-1", "speed Alert", "Track validation failure");
        verify(trackRepository, never()).saveAll(any());
    }
}
