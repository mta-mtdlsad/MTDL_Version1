package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.repository.TrackRepository;
import com.mtms.util.ReplaySpeed;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReplayService Class Test")
public class ReplayServiceTest {

    @Mock
    private TrackRepository track_repo;

    @InjectMocks
    private ReplayService replayService;

    private Track track;

    private LocalDateTime start_time;

    private LocalDateTime end_time;

    // Runs before each test
    @BeforeEach
    void init(TestInfo testInfo, TestReporter testReporter) {
        // New track for each test
        track = new Track();
        track.setTrackNumber("TRK-10001");
        // Set the replay timeframe
        start_time = LocalDateTime.of(2026, 11, 1, 10, 0);
        end_time = LocalDateTime.of(2026, 11, 1, 11, 0);
        // Publish the test info
        testReporter.publishEntry("\nRunning " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    //Replay tracks successfully
    @Test
    @DisplayName("Test Replay Tracks Functionality")
    void replayTracksTest() {
        // Simulate the repository returning one track
        when(track_repo.getAllTracksTimeframe(start_time, end_time)).thenReturn(List.of(track));
        // Call the replay method
        List<Track> result = replayService.replayTracks(ReplaySpeed.NORMAL, start_time, end_time, t -> {});
        // Check that one track was returned
        assertEquals(1, result.size(), "Test Failed: Incorrect number of tracks");
        // Check that the correct track was returned
        assertEquals(track, result.get(0), "Test Failed: Incorrect track returned");
        // Verify repository was called
        verify(track_repo).getAllTracksTimeframe(start_time, end_time);
    }

    // Replay when no tracks exist
    @Test
    @DisplayName("Test Replay With Empty Timeframe")
    void replayEmptyTest() {
        // Simulate no tracks in the timeframe
        when(track_repo.getAllTracksTimeframe(start_time, end_time)).thenReturn(List.of());
        // Call the replay method
        List<Track> result = replayService.replayTracks(ReplaySpeed.NORMAL, start_time, end_time, t -> {});
        // Check that the list is empty
        assertTrue(result.isEmpty(), "Test Failed: Expected an empty track list");

    }

    // Replay with an invalid timeframe
    @Test
    @DisplayName("Test Invalid Replay Timeframe")
    void invalidTimeframeTest() {
        // Start time is after end time
        assertThrows(IllegalArgumentException.class, () ->
                replayService.replayTracks(ReplaySpeed.NORMAL, end_time, start_time, t -> {}),
                "Test Failed: Expected IllegalArgumentException");
        // Repository should not be called since the timeframe is invalid
        verifyNoInteractions(track_repo);
    }

    // Check that the track is replayed
    @Test
    @DisplayName("Test Track Replay Callback")
    void replayCallbackTest() {
        // Simulate one track being returned
        when(track_repo.getAllTracksTimeframe(start_time, end_time)).thenReturn(List.of(track));
        // Store replayed tracks
        List<Track> replayedTracks = new ArrayList<>();
        // Replay the tracks
        // replayedTracks::add == t->replayedTracks.add(t)
        replayService.replayTracks(ReplaySpeed.NORMAL, start_time, end_time, replayedTracks::add);
        // Check that the track was replayed
        assertEquals(1, replayedTracks.size(), "Test Failed: Track was not replayed");
        assertEquals(track, replayedTracks.get(0), "Test Failed: Wrong track replayed");
    }
}