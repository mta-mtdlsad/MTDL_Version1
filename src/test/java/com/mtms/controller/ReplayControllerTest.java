package com.mtms.controller;

import com.mtms.model.Track;
import com.mtms.service.ReplayService;
import com.mtms.util.ReplaySpeed;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReplayController Class Test")
public class ReplayControllerTest {
    @Mock
    private ReplayService replayService;

    @InjectMocks
    private ReplayController replayController;

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
        when(replayService.replayTracks(eq(ReplaySpeed.NORMAL),eq(start_time), eq(end_time), any())).thenReturn(List.of(track));
        // Call the replay controller
        SseEmitter result = replayController.replayTracks(ReplaySpeed.NORMAL, start_time, end_time);
        // Check that the emitter was created
        assertNotNull(result, "Test Failed: Replay Emitter was not created");
        // Check the replay service was called
        verify(replayService, timeout(1000)).replayTracks(eq(ReplaySpeed.NORMAL),eq(start_time), eq(end_time), any());

    }

    // Replay when no tracks exist
    @Test
    @DisplayName("Test Replay With Empty Timeframe")
    void replayEmptyTest() {
        // Simulate no tracks in the timeframe
        when(replayService.replayTracks(eq(ReplaySpeed.NORMAL),eq(start_time), eq(end_time), any())).thenReturn(List.of());
        // Call the replay controller
        SseEmitter result = replayController.replayTracks(ReplaySpeed.NORMAL, start_time, end_time);
        // Check that the emitter was created
        assertNotNull(result, "Test Failed: Replay Emitter was not created");
        // Check the replay service was called
        verify(replayService, timeout(1000)).replayTracks(eq(ReplaySpeed.NORMAL),eq(start_time), eq(end_time), any());

    }

    // Replay with an invalid timeframe
    @Test
    @DisplayName("Test Invalid Replay Timeframe")
    void invalidTimeframeTest() {
        // Start time is after end time
        assertThrows(IllegalArgumentException.class, () ->
                        replayController.replayTracks(ReplaySpeed.NORMAL, end_time, start_time),
                "Test Failed: Expected IllegalArgumentException");
        // Repository should not be called since the timeframe is invalid
        verifyNoInteractions(replayService);
    }

    // Check that the track is replayed
    @Test
    @DisplayName("Test Track Replay Callback")
    void replayCallbackTest() {
        // Simulate the service receiving a call back
        when(replayService.replayTracks(eq(ReplaySpeed.NORMAL),eq(start_time), eq(end_time), any())).thenAnswer(invocation -> {
            // Get the callback passed by the controller
            Consumer<Track> callback = invocation.getArgument(3);
            // Simulate replaying one track
            callback.accept(track);
            // Return the replayed track
            return List.of(track);
        });
        // Store replayed tracks
        SseEmitter result = replayController.replayTracks(ReplaySpeed.NORMAL, start_time, end_time);
        // Check that the emitter was created
        assertNotNull(result, "Test Failed: Replay Emitter was not created");
        //verifying the controller called the service
        verify(replayService, timeout(1000)).replayTracks(eq(ReplaySpeed.NORMAL),eq(start_time), eq(end_time), any());
    }
}
