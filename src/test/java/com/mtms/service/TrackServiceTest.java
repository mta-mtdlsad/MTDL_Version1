package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("TrackService Class Test")
@ExtendWith(MockitoExtension.class)
public class TrackServiceTest {

    @Mock
    private TrackRepository track_repo;
    @InjectMocks
    private TrackService trackService;

    private Track track1, track2 ,track3;
    private List<Track> listOfTracks;

    @BeforeEach
    public void beforeAll() {
        track1 = new Track("LINK-A", 1, "TRK1001", 10.0, 70.0, 900.0, "HOSTILE", "ACTIVE", LocalDateTime.now());
        track2 = new Track("LINK-A", 2, "TRK1002", 10.0, 70.0, 300.0, "FRIEND", "INACTIVE", LocalDateTime.now());
        track3 = new Track("LINK-B", 1, "TRK2001", 10.0, 70.0, 500.0, "FRIEND", "ACTIVE", LocalDateTime.now());
        listOfTracks = Arrays.asList(track1, track2, track3);
    }

    @Nested
    @Tag("TracksBasicCRUD")
    @DisplayName("Test saveTrack Functionality")
    class saveTrackTest {
        @Test
        @DisplayName("When Track is not in database table")
        public void saveNewTrackTest() {
            when(track_repo.existsBySourceIdAndSequenceNumber("LINK-A", 1)).thenReturn(false);
            when(track_repo.save(track1)).thenReturn(track1);

            boolean result = trackService.saveTrack(track1);
            assertTrue(result);
            verify(track_repo, times(1)).existsBySourceIdAndSequenceNumber("LINK-A", 1);
            verify(track_repo, times(1)).save(track1);
        }
        @Test
        @DisplayName("When Track is in database table")
        public void saveExistsTrackTest() {
            when(track_repo.existsBySourceIdAndSequenceNumber("LINK-A", 1)).thenReturn(true);

            boolean result = trackService.saveTrack(track1);
            assertFalse(result);
            verify(track_repo, times(1)).existsBySourceIdAndSequenceNumber("LINK-A", 1);
            verify(track_repo, times(0)).save(track1);
        }
    }

    @Test
    @Tag("TracksBasicCRUD")
    @DisplayName("Test getAllTracks Functionality")
    public void getAllTracksTest() {
        when(track_repo.findAll()).thenReturn(listOfTracks);
        assertFalse(trackService.getAllTracks().isEmpty());
        verify(track_repo, times(1)).findAll();
    }

    @Nested
    @Tag("TracksBasicCRUD")
    @DisplayName("Test findTracksByNumber Functionality")
    class findTracksByNumberTest {
        @Test
        @DisplayName("When TrackNumber is in database table")
        public void findTracksByNumberExistsTest() {
            when(track_repo.findTracksByTrackNumber("TRK1001")).thenReturn(listOfTracks);

            assertEquals(listOfTracks, trackService.findTracksByNumber("TRK1001"));
            verify(track_repo, times(1)).findTracksByTrackNumber("TRK1001");
        }
        @Test
        @DisplayName("When TrackNumber is not in database table")
        public void findTracksByNumberDoesntExistTest() {
            when(track_repo.findTracksByTrackNumber("TRK1001")).thenReturn(new ArrayList<>());

            assertEquals(new ArrayList<>(), trackService.findTracksByNumber("TRK1001"));
            verify(track_repo, times(1)).findTracksByTrackNumber("TRK1001");
        }
    }

    @Nested
    @Tag("TracksBasicCRUD")
    @DisplayName("Test deleteTracksByTrackNumber Functionality")
    class deleteTracksByTrackNumberTest {
        @Test
        @DisplayName("When TrackNumber is in database table")
        public void deleteTracksByTrackNumberExistsTest() {
            when(track_repo.deleteAllByTrackNumber("TRK1001")).thenReturn(1L);

            assertEquals(true, trackService.deleteTracksByTrackNumber("TRK1001"));
            verify(track_repo, times(1)).deleteAllByTrackNumber("TRK1001");
        }
        @Test
        @DisplayName("When TrackNumber is not in database table")
        public void deleteTracksByTrackNumberDoesntExistTest() {
            when(track_repo.deleteAllByTrackNumber("TRK1001")).thenReturn(0L);

            assertEquals(false, trackService.deleteTracksByTrackNumber("TRK1001"));
            verify(track_repo, times(1)).deleteAllByTrackNumber("TRK1001");
        }
    }

    @Nested
    @Tag("TracksAdvancedCRUD")
    @DisplayName("Test getAllTrackHistory Functionality")
    class getAllTrackHistoryTest {
        @Test
        @DisplayName("When TrackNumber is in database table")
        public void findTracksByNumberExistsTest() {
            when(track_repo.findTracksByTrackNumber("TRK1001")).thenReturn(listOfTracks);

            assertEquals(listOfTracks, trackService.findTracksByNumber("TRK1001"));
            verify(track_repo, times(1)).findTracksByTrackNumber("TRK1001");
        }
        @Test
        @DisplayName("When TrackNumber is not in database table")
        public void findTracksByNumberDoesntExistTest() {
            when(track_repo.findTracksByTrackNumber("TRK1001")).thenReturn(new ArrayList<>());

            assertEquals(new ArrayList<>(), trackService.findTracksByNumber("TRK1001"));
            verify(track_repo, times(1)).findTracksByTrackNumber("TRK1001");
        }
    }
}
