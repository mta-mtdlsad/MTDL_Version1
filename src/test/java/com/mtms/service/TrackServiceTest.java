package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.main.MTMSApplication;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = MTMSApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TrackServiceTest {

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @BeforeEach
    public void clearRepository(){
        trackRepository.deleteAll();
    }

    @Test
    @Tag("Track")
    public void processTrackStringTest(){
        String trackStringValid = "LINK-A|1|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:10:00";
        String trackStringInvalid = "no tracks";
        assertAll(
                () -> assertTrue(trackService.processTrackString(trackStringValid)),
                () -> assertFalse(trackService.processTrackString(trackStringInvalid))
        );
    }

    @Test
    @Tag("Track")
    public void processTrackTest(){
        Track trackValid = new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, LocalDateTime.now(), LocalDateTime.now());
        Track trackInvalid = new Track("TRK1004", 100, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, LocalDateTime.now(), LocalDateTime.now());
        assertAll(
                () -> assertTrue(trackService.processTrack(trackValid)),
                () -> assertFalse(trackService.processTrack(trackInvalid))
        );
    }

    @Test
    @Tag("Track")
    public void hasDuplicatesTest(){
        Track trackDuplicate = new Track("TRK1005", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 2, LocalDateTime.now(), LocalDateTime.now());
        trackService.processTrack(trackDuplicate);
        Track trackUnique = new Track("TRK1006", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-E", 1, LocalDateTime.now(), LocalDateTime.now());
        assertAll(
                () -> assertFalse(trackService.hasDuplicates(trackUnique)),
                () -> assertTrue(trackService.hasDuplicates(trackDuplicate))
        );
    }


    @Test
    @Tag("Track")
    public void isOutOfOrderTest(){
        Track trackOrdered = new Track("TRK1005", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 2, LocalDateTime.now(), LocalDateTime.now());
        trackService.processTrack(trackOrdered);
        Track trackNotOrdered = new Track("TRK1005", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, LocalDateTime.now(), LocalDateTime.now());
        assertAll(
                () -> assertFalse(trackService.isOutOfOrder(trackOrdered)),
                () -> assertTrue(trackService.isOutOfOrder(trackNotOrdered))
        );
    }

    @Test
    @Tag("Track")
    public void parsingTrackStringTest(){
        String trackStringInvalid = "|450|FRIEND|ACTIVE|2026-11-02T09:10:00";
        String trackStringValid = "LINK-A|1|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:10:00";
        LocalDateTime time = LocalDateTime.parse("2026-11-02T09:10:00");
        Track track= new Track("TRK1001", 12.971600, 77.594600, 450, "FRIEND",
                "ACTIVE", "LINK-A", 1, time, time);
        assertAll(
                () -> assertThrows(RuntimeException.class, () -> trackService.parsingTrackString(trackStringInvalid)),
                () -> assertThat(trackService.parsingTrackString(trackStringValid))
                        .usingRecursiveComparison()
                        .ignoringFields("receivedTime")
                        .isEqualTo(track)
        );
    }

    @Test
    @Tag("Track")
    public void findAllTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time, time);

        assertThat(trackService.findAllTracks()).isEmpty();

        trackService.processTrack(track1);
        trackService.processTrack(track2);
        List<Track> listOfTracks = Arrays.asList(track1, track2);

        assertThat(trackService.findAllTracks())
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("receivedTime")
                        .containsExactlyElementsOf(listOfTracks);
    }

    @Test
    @Tag("Track")
    public void findTrackByNumTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        trackService.processTrack(track);
        List<Track> listOfTracks = Arrays.asList(track);
        assertAll(
                () -> assertThat(trackService.findTrackByNum("TRK1004"))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("receivedTime")
                        .containsExactlyElementsOf(listOfTracks),
                () -> assertThat(trackService.findTrackByNum("TRK1005")).isEmpty()
        );
    }

    @Test
    @Tag("Track")
    public void deleteTrackByNumTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        trackService.processTrack(track);

        assertAll(
                () -> assertFalse(trackService.deleteTrackByNum("TRK1005")),
                () -> assertTrue(trackService.deleteTrackByNum("TRK1004"))
        );
    }

    @Test
    @Tag("Track")
    public void getCurrentViewTest() {
        LocalDateTime time1 = LocalDateTime.parse("2026-11-03T12:45:30");
        LocalDateTime time2 = LocalDateTime.parse("2026-11-05T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time1, time1);
        Track track2 = new Track("TRK1004", 70, 100, 500, "UNKNOWN",
                "ACTIVE", "LINK-D", 2, time2, time2);

        assertThat(trackService.getCurrentView()).isEmpty();

        trackService.processTrack(track1);
        trackService.processTrack(track2);
        List<Track> listOfTracks = Arrays.asList(track2);

        assertThat(trackService.getCurrentView())
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("receivedTime")
                        .containsExactlyElementsOf(listOfTracks);
    }

    @Test
    @Tag("Track")
    public void getFullHistoryTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 70, 100, 500, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time, time);
        Track track3= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time, time);
        trackService.processTrack(track1);
        trackService.processTrack(track2);
        trackService.processTrack(track3);
        List<Track> listOfTracks = Arrays.asList(track1, track3);
        assertAll(
                () -> assertThat(trackService.getFullHistory("TRK1004", "LINK-D"))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("receivedTime")
                        .containsExactlyElementsOf(listOfTracks),
                () -> assertThat(trackService.getFullHistory("TRK1005", "LINK-D")).isEmpty()
        );
    }

    @Test
    @Tag("Track")
    public void processingCounterTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1001", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-A", 1, time, time);
        Track track2 = new Track("TRK1001", 70, 100, -2, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time, time);

        List<String> listOfCountsNoTracks = new ArrayList<>(Arrays.asList("Accepted Messages: 0",
                "Rejected Messages: 0",
                "Received Messages: 0",
                "Duplicate Messages: 0",
                "Processing Failure: 0"));

        assertThat(trackService.processingCounter()).isEqualTo(listOfCountsNoTracks);

        trackService.processTrack(track1);
        trackService.processTrack(track2);
        trackService.processTrack(track1);

        List<String> listOfCounts = new ArrayList<>(Arrays.asList("Accepted Messages: 1",
                "Rejected Messages: 2",
                "Received Messages: 3",
                "Duplicate Messages: 1",
                "Processing Failure: 3"));

        assertThat(trackService.processingCounter()).isEqualTo(listOfCounts);
    }

    @Test
    @Tag("Track")
    public void getFilteredTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1001", 70, 100, 20, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time, time);
        trackService.processTrack(track1);
        trackService.processTrack(track2);
        List<Track> listOfTracks = Arrays.asList(track2);
        assertAll(
                () -> assertThat(trackService.getFilteredTracks("UNKNOWN", "", null))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("receivedTime")
                        .containsExactlyElementsOf(listOfTracks),
                () -> assertThat(trackService.getFilteredTracks("", "", 500.0)).isEmpty()
        );
    }


    @Test
    @Tag("Track")
    public void getAdvancedFilteredTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 70, 100, 20, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time, time);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<Track> listOfTracks = Arrays.asList(track2);
        assertAll(
                () -> assertThat(trackService.getAdvancedFilteredTracks("", "TRK1004", "",
                        "ACTIVE", "0", "20", "", ""))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("receivedTime")
                        .containsExactlyElementsOf(listOfTracks),
                () -> assertThat(trackService.getAdvancedFilteredTracks("LINK-D", "TRK1001", "",
                        "", "", "100", "", "")).isEmpty()
        );
    }

}
