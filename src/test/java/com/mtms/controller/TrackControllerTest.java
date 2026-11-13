package com.mtms.controller;

import com.mtms.model.Track;
import com.mtms.service.MessageProcessorService;
import com.mtms.service.TrackService;
import com.mtms.util.FilterParameters;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("TrackController Class Test")
@ExtendWith(MockitoExtension.class)
public class TrackControllerTest {

    @Mock
    private TrackService trackService;
    @Mock
    private MessageProcessorService processor;
    @InjectMocks
    TrackController trackController;

    private Track track1, track2, track3, track4;
    private List<Track> listOfTracks;

    @BeforeEach
    public void beforeAll() {
        track1 = new Track("LINK-A", 1, "TRK1001", 10.0, 70.0, 550.0, "FRIEND", "ACTIVE", LocalDateTime.now());
        track2 = new Track("LINK-A", 2, "TRK1001", 10.0, 70.0, 300.0, "FRIEND", "INACTIVE", LocalDateTime.now());
        track3 = new Track("LINK-B", 1, "TRK2001", 10.0, 70.0, 500.0, "FRIEND", "ACTIVE", LocalDateTime.now());
        track4 = new Track("LINK-C", 1, "TRK3001", 15.0, 75.0, 900.0, "FRIEND", "ACTIVE", LocalDateTime.now());
        listOfTracks = Arrays.asList(track1, track2, track3, track4);
    }

    @Nested
    @Tag("TracksControllerBasicCRUD")
    @DisplayName("Test addTrack Functionality")
    class addTrackTest {
        @Test
        @DisplayName("When Track is ACCEPTED")
        public void addTrackACCEPTEDTest() {
            MessageProcessorService.ProcessingResult result = new MessageProcessorService.ProcessingResult(
                    MessageProcessorService.Outcome.ACCEPTED,
                    false);
            when(processor.processDetailed(track1)).thenReturn(result);
            assertEquals(ResponseEntity.status(201).body(result), trackController.addTrack(track1));
            verify(processor, times(1)).processDetailed(track1);
        }

        @Test
        @DisplayName("When Track is REJECTED")
        public void addTrackREJECTEDTest() {
            MessageProcessorService.ProcessingResult result = new MessageProcessorService.ProcessingResult(
                    MessageProcessorService.Outcome.REJECTED,
                    false);
            when(processor.processDetailed(track1)).thenReturn(result);
            assertEquals(ResponseEntity.status(422).body(result), trackController.addTrack(track1));
            verify(processor, times(1)).processDetailed(track1);
        }

        @Test
        @DisplayName("When Track is DUPLICATE")
        public void addTrackDUPLICATETest() {
            MessageProcessorService.ProcessingResult result = new MessageProcessorService.ProcessingResult(
                    MessageProcessorService.Outcome.DUPLICATE,
                    false);
            when(processor.processDetailed(track1)).thenReturn(result);
            assertEquals(ResponseEntity.status(409).body(result), trackController.addTrack(track1));
            verify(processor, times(1)).processDetailed(track1);
        }

        @Test
        @DisplayName("When Track is FAILURE")
        public void addTrackFAILURETest() {
            MessageProcessorService.ProcessingResult result = new MessageProcessorService.ProcessingResult(
                    MessageProcessorService.Outcome.FAILURE,
                    false);
            when(processor.processDetailed(track1)).thenReturn(result);
            assertEquals(ResponseEntity.status(500).body(result), trackController.addTrack(track1));
            verify(processor, times(1)).processDetailed(track1);
        }
    }

    @Test
    @Tag("TracksControllerBasicCRUD")
    @DisplayName("Test getAllTracks Functionality")
    public void getAllTracksTest() {
        when(trackService.getAllTracks()).thenReturn(listOfTracks);
        assertEquals(listOfTracks, trackController.getAllTracks());
        verify(trackService, times(1)).getAllTracks();
    }

    @Nested
    @Tag("TracksControllerBasicCRUD")
    @DisplayName("Test searchTracks Functionality")
    class searchTrackTest {
        @Test
        @DisplayName("When TrackNumber exists in database table")
        public void searchTracksExistsTest() {
            when(trackService.findTracksByNumber("TRK1001")).thenReturn(listOfTracks);
            assertEquals(listOfTracks, trackController.searchTracks("TRK1001"));
            verify(trackService, times(1)).findTracksByNumber("TRK1001");
        }
        @Test
        @DisplayName("When TrackNumber doesn't exist in database table")
        public void searchTracksDoesntExistTest() {
            when(trackService.findTracksByNumber("TRK1001")).thenReturn(new ArrayList<>());
            assertEquals(new ArrayList<>(), trackController.searchTracks("TRK1001"));
            verify(trackService, times(1)).findTracksByNumber("TRK1001");
        }
    }

    @Nested
    @Tag("TracksControllerBasicCRUD")
    @DisplayName("Test deleteTrack Functionality")
    class deleteTrackTest {
        @Test
        @DisplayName("When TrackNumber is in database table")
        public void deleteTrackExistsTest() {
            when(trackService.deleteTracksByTrackNumber("TRK1001")).thenReturn(true);
            assertEquals(true, trackController.deleteTrack("TRK1001"));
            verify(trackService, times(1)).deleteTracksByTrackNumber("TRK1001");
        }
        @Test
        @DisplayName("When TrackNumber is not in database table")
        public void deleteTrackDoesntExistTest() {
            when(trackService.deleteTracksByTrackNumber("TRK1001")).thenReturn(false);
            assertEquals(false, trackController.deleteTrack("TRK1001"));
            verify(trackService, times(1)).deleteTracksByTrackNumber("TRK1001");
        }
    }

    @Nested
    @Tag("TracksControllerAdvancedCRUD")
    @DisplayName("Test searchHistoryTracks Functionality")
    class searchHistoryTracksTest {
        @Test
        @DisplayName("When SourceId and TrackNumber exist in database table")
        public void searchHistoryTracksExistsTest() {
            when(trackService.getAllTrackHistory("TRK1001","LINK-A")).thenReturn(listOfTracks);
            assertEquals(listOfTracks, trackController.searchHistoryTracks("LINK-A", "TRK1001"));
            verify(trackService, times(1)).getAllTrackHistory("TRK1001","LINK-A");
        }
        @Test
        @DisplayName("When SourceId and TrackNumber don't exist in database table")
        public void searchHistoryTracksDoesntExistTest() {
            when(trackService.getAllTrackHistory("TRK5005","LINK-F")).thenReturn(new ArrayList<>());
            assertEquals(new ArrayList<>(), trackController.searchHistoryTracks("LINK-F","TRK5005"));
            verify(trackService, times(1)).getAllTrackHistory("TRK5005","LINK-F");
        }
    }

    @Nested
    @Tag("TracksControllerAdvancedCRUD")
    @DisplayName("Test Filter Functionality")
    class filterTest {
        @Test
        @Tag("TracksControllerAdvancedCRUD")
        @DisplayName("Test filterTracks without input Functionality")
        public void filterTracksNoInputTest() {
            // For filter: SourceId = Link-B
            when(trackService.filterTracks(any())).thenReturn(Arrays.asList(track3));

            assertEquals(Arrays.asList(track3), trackController.filterTracks(new FilterParameters()));
            verify(trackService, times(1)).filterTracks(any());
        }
        @Test
        @Tag("TracksControllerAdvancedCRUD")
        @DisplayName("Test filterTracks with input Functionality")
        public void filterTracksInputTest() {
            // For filter: identity, status, and minSpeed
            when(trackService.filterTracks(any())).thenReturn(Arrays.asList(track4));

            assertEquals(Arrays.asList(track4), trackController.filterTracks("identity", "status", "minSpeed"));
            verify(trackService, times(1)).filterTracks(any());
        }
    }
}
