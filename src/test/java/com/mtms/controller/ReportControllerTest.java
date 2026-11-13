package com.mtms.controller;

import com.mtms.model.Track;
import com.mtms.service.AlertService;
import com.mtms.service.TrackService;
import com.mtms.util.FilterParameters;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ReportController Class Test")
@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    @Mock
    private TrackService trackService;
    @Mock
    private AlertService alertService;
    @InjectMocks
    ReportController reportController;

    private Track track1, track2 ,track3, track4;

    @BeforeEach
    public void beforeAll() {
        track1 = new Track("LINK-A", 1, "TRK1001", 10.0, 70.0, 550.0, "FRIEND", "ACTIVE", LocalDateTime.now());
        track2 = new Track("LINK-A", 2, "TRK1001", 10.0, 70.0, 300.0, "FRIEND", "INACTIVE", LocalDateTime.now());
        track3 = new Track("LINK-B", 1, "TRK2001", 10.0, 70.0, 500.0, "FRIEND", "ACTIVE", LocalDateTime.now());
        track4 = new Track("LINK-C", 1, "TRK3001", 15.0, 75.0, 900.0, "FRIEND", "ACTIVE", LocalDateTime.now());
    }

    @Test
    @DisplayName("Test getStatistics Functionality")
    public void getStatisticsTest() {
        Map<String, Integer> identityStatistics = Map.of("FRIEND",0, "HOSTILE",0, "NEUTRAL",0, "UNKNOWN",0);
        Map<String, Integer> severityStatistics = Map.of("ACTIVE",0, "INACTIVE",0);
        when(trackService.getIdentityStatistics()).thenReturn(identityStatistics);
        when(alertService.getSeverityStatistics()).thenReturn(severityStatistics);
        assertEquals(Arrays.asList(identityStatistics, severityStatistics), reportController.getStatistics());
        verify(trackService, times(1)).getIdentityStatistics();
        verify(alertService, times(1)).getSeverityStatistics();
    }

    @Nested
    @DisplayName("Test Correlation Functionality")
    class correlationTest {
        @Test
        @DisplayName("When correlation to one given track is returned (track doesn't exist)")
        public void correlateTrackOneDoesntExistTest() {
            when(trackService.filterTracks(any())).thenReturn(null);
            assertEquals(null, reportController.getCorrelatedTracks("LINK-A", "1"));
            verify(trackService, times(1)).filterTracks(any());
        }
        @Test
        @DisplayName("When correlation to one given track is returned (track exists)")
        public void correlateTrackOneExistsTest() {
            when(trackService.filterTracks(any())).thenReturn(Arrays.asList(track1));
            when(trackService.correlateTrack(track1, null)).thenReturn(Arrays.asList(track3));
            assertEquals(Arrays.asList(track3), reportController.getCorrelatedTracks("LINK-A", "1"));
            verify(trackService, times(1)).filterTracks(any());
            verify(trackService, times(1)).correlateTrack(track1, null);
        }
        @Test
        @DisplayName("When any correlation is returned")
        public void correlateTracksAnyTest() {
            Map<Track, List<Track>> result = Map.of(track1, Arrays.asList(track3));
            when(trackService.correlateTracks()).thenReturn(result);
            assertEquals(result, reportController.getCorrelatedTracks());
            verify(trackService, times(1)).correlateTracks();
        }
    }
}
