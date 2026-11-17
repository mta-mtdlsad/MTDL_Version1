package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReplayService Class Test")
public class ReplayServiceTest {

    @Mock
    private TrackRepository repository;

    @InjectMocks
    private ReplayService replayService;

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

    // Run full replay when database has no tracks
    @Test
    @DisplayName("Test Run Replay When Empty")
    void runReplayEmptyTest() {
        // Simulate empty track list
        when(repository.findAllByOrderByCreatedTimeAscTrackIdAsc()).thenReturn(List.of());

        // Call replay method
        replayService.runReplay();

        // Verify repository query was executed
        verify(repository).findAllByOrderByCreatedTimeAscTrackIdAsc();
    }

    // Run windowed replay over a valid timestamp range
    @Test
    @DisplayName("Test Run Replay Window With Track")
    void runReplayWindowTest() {
        // Simulate returning one track inside the window
        Track track = new Track();
        when(repository.selectByTimeWindow(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(track));

        // Call windowed replay with 1ms delay
        replayService.runReplayWindow("2026-11-16 09:00:00", "2026-11-16 10:00:00", 1);

        // Verify windowed query was called
        verify(repository).selectByTimeWindow(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
