package com.mtms.service;

import com.mtms.domain.ReplaySpeed;
import com.mtms.domain.Track;
import com.mtms.main.MTMSApplication;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = MTMSApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReplayServiceTest {

    @Autowired
    private ReplayService replayService;

    @Autowired
    private TrackRepository trackRepository;

    @Test
    @Tag("Replay")
    public void replayTracksTest(){
        LocalDateTime time1 = LocalDateTime.parse("2026-11-03T12:45:30");
        LocalDateTime time2 = LocalDateTime.parse("2026-11-04T12:45:30");
        LocalDateTime time3 = LocalDateTime.parse("2026-11-05T12:45:30");
        Track track1 = new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time1, time1);
        Track track2 = new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time2, time2);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<Track> listOfTracks = Arrays.asList(track1, track2);
        assertAll(
                () -> assertThat(replayService.replayTracks(ReplaySpeed.NORMAL, time1, time2))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("receivedTime")
                        .containsExactlyElementsOf(listOfTracks),
                () -> assertThat(replayService.replayTracks(ReplaySpeed.NORMAL, time3, time3)).isEmpty()
        );
    }


}
