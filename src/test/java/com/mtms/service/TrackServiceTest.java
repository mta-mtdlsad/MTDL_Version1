package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.repository.TrackRepository;
import com.mtms.util.FilterParameters;
import com.mtms.util.TrackSpecifications;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

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
        track1 = new Track("LINK-A", 1, "TRK1001", 10.0, 70.0, 900.0, "FRIEND", "ACTIVE", LocalDateTime.now());
        track2 = new Track("LINK-A", 2, "TRK1001", 10.0, 70.0, 300.0, "FRIEND", "INACTIVE", LocalDateTime.now());
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
        @DisplayName("When SourceId and TrackNumber exist in database table")
        public void getAllTrackHistoryExistsTest() {
            when(track_repo.findTracksByTrackNumberAndSourceId("TRK1001","LINK-A")).thenReturn(listOfTracks);

            assertEquals(listOfTracks, trackService.getAllTrackHistory("TRK1001","LINK-A"));
            verify(track_repo, times(1)).findTracksByTrackNumberAndSourceId("TRK1001","LINK-A");
        }
        @Test
        @DisplayName("When SourceId and TrackNumber don't exist in database table")
        public void getAllTrackHistoryDoesntExistTest() {
            when(track_repo.findTracksByTrackNumberAndSourceId("TRK5005","LINK-F")).thenReturn(new ArrayList<>());

            assertEquals(new ArrayList<>(), trackService.getAllTrackHistory("TRK5005","LINK-F"));
            verify(track_repo, times(1)).findTracksByTrackNumberAndSourceId("TRK5005","LINK-F");
        }
    }

    @Nested
    @Tag("TracksAdvancedCRUD")
    @DisplayName("Test getHighestSequence Functionality")
    class getHighestSequenceTest {
        @Test
        @DisplayName("When SourceId and TrackNumber exist in database table")
        public void getHighestSequenceExistsTest() {
            when(track_repo.findTracksBySourceId("LINK-A")).thenReturn(listOfTracks);

            assertEquals(2, trackService.getHighestSequence("LINK-A"));
            verify(track_repo, times(1)).findTracksBySourceId("LINK-A");
        }
        @Test
        @DisplayName("When SourceId and TrackNumber don't exist in database table")
        public void getHighestSequenceDoesntExistTest() {
            when(track_repo.findTracksBySourceId("LINK-F")).thenReturn(new ArrayList<>());

            assertEquals(0, trackService.getHighestSequence("LINK-F"));
            verify(track_repo, times(1)).findTracksBySourceId("LINK-F");
        }
    }

    @Test
    @Tag("TracksAdvancedCRUD")
    @DisplayName("Test getIdentityStatistics Functionality")
    public void getIdentityStatisticsTest() {
        Map<String, Integer> statistics = Map.of("FRIEND",0, "HOSTILE",0, "NEUTRAL",0, "UNKNOWN",0);
        when(track_repo.countByIdentityType()).thenReturn(new ArrayList<>());

        assertEquals(statistics, trackService.getIdentityStatistics());
        verify(track_repo, times(1)).countByIdentityType();
    }

    @Test
    @Tag("TracksAdvancedCRUD")
    @DisplayName("Test filterTracks Functionality")
    public void filterTracksTest() {
        when(track_repo.findAll(
                ArgumentMatchers.<Specification<Track>>any(),
                any(Sort.class)
        )).thenReturn(listOfTracks);

        assertEquals(listOfTracks, trackService.filterTracks(new FilterParameters()));
        verify(track_repo, times(1)).findAll(
                ArgumentMatchers.<Specification<Track>>any(),
                any(Sort.class)
        );
    }

    @Nested
    @Tag("TracksAdvancedCRUD")
    @DisplayName("Test Correlation Functionality")
    class correlationTest {
        @Test
        @DisplayName("Util function that calculate distance in km")
        public void calculateDistanceTest() {
            assertEquals(15.605156575449083, trackService.calculate_distance(10.0, 70.0, 10.1, 70.1));
        }
        @Test
        @DisplayName("When correlation to one given track is returned")
        public void correlateTrackOneTest() {
            List<Track> results = new ArrayList<>();
            when(track_repo.findAll(ArgumentMatchers.<Specification<Track>>any())).thenReturn(results);

            assertEquals(results, trackService.correlateTrack(track1, listOfTracks));
            verify(track_repo, times(1)).findAll(ArgumentMatchers.<Specification<Track>>any());
        }
        @Test
        @DisplayName("When any correlation is returned")
        public void correlateTracksAnyTest() {
            Map<Track, List<Track>> correlationMap = new HashMap<>();
            assertEquals(correlationMap, trackService.correlateTracks());
        }
    }
}
