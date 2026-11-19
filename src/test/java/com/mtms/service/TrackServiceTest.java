package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrackService Class Test")
public class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private PathService pathService;

    @Mock
    private ValidationService validationService;

    @Mock
    private AlertService alertService;

    @Mock
    private SourceService sourceService;

    @InjectMocks
    private TrackService trackService;

    private Track track;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        // Initialize a sample track
        track = new Track();
        track.setSourceId("SRC-01");
        track.setSequenceNumber(1);
        track.setTrackNumber("TRK-10001");
        track.setLatitude(25.0);
        track.setLongitude(55.0);
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Search tracks by track number
    @Test
    @DisplayName("Test Search Tracks")
    void searchTracksTest() {
        // Simulate repository returning matching track
        when(trackRepository.findByTrackNumberOrderByCreatedTimeDescTrackIdDesc("TRK-10001"))
                .thenReturn(List.of(track));

        // Call search method
        List<Track> results = trackService.searchTracks("TRK-10001");

        // Check returned list size
        assertEquals(1, results.size(), "Test Failed: Expected 1 track");
    }

    // Process and save a valid new track
    @Test
    @DisplayName("Test Process Valid Track")
    void processValidTrackTest() {
        // Simulate non-duplicate track with zero validation errors
        when(trackRepository.existsBySourceIdAndSequenceNumber("SRC-01", 1)).thenReturn(false);
        when(validationService.getValidationErrors(track)).thenReturn(List.of());

        // Call processTrack
        trackService.processTrack(track);

        // Verify source metrics synced as ACCEPTED and track saved
        verify(sourceService).syncSourceMetrics("SRC-01", 1, "ACCEPTED");
        verify(trackRepository).save(track);
    }

    // Handle duplicate track without saving to repository
    @Test
    @DisplayName("Test Process Duplicate Track")
    void processDuplicateTrackTest() {
        // Simulate existing sourceId and sequenceNumber
        when(trackRepository.existsBySourceIdAndSequenceNumber("SRC-01", 1)).thenReturn(true);

        // Call processTrack
        trackService.processTrack(track);

        // Verify duplicate alert, DUPLICATE metric sync, and no save
        verify(alertService).createAlert("TRK-10001", "SRC-01", "Sequence Number Repeated Alert", "Duplicate message");
        verify(sourceService).syncSourceMetrics("SRC-01", 1, "DUPLICATE");
        verify(trackRepository, never()).save(any());
    }

    // Reject invalid track and log validation alert
    @Test
    @DisplayName("Test Process Invalid Track")
    void processInvalidTrackTest() {
        // Simulate non-duplicate track failing validation
        when(trackRepository.existsBySourceIdAndSequenceNumber("SRC-01", 1)).thenReturn(false);
        when(validationService.getValidationErrors(track)).thenReturn(List.of("speed Alert"));

        // Call processTrack
        trackService.processTrack(track);

        // Verify REJECTED metric sync and alert creation
        verify(sourceService).syncSourceMetrics("SRC-01", 1, "REJECTED", 1);
        verify(alertService).createAlert("TRK-10001", "SRC-01", "speed Alert", "Track validation failure");
    }
}
