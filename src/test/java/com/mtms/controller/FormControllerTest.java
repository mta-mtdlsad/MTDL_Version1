package com.mtms.controller;

import com.mtms.domain.ReplaySpeed;
import com.mtms.domain.Track;
import com.mtms.main.MTMSApplication;
import com.mtms.repository.TrackRepository;
import com.mtms.service.ReplayService;
import com.mtms.service.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MTMSApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FormControllerTest {

    @Autowired
    private FormController FormController;

    @Autowired
    private TrackRepository trackRepository;

    @BeforeEach
    public void clearRepository(){
        trackRepository.deleteAll();
    }

    @Test
    @Tag("FormController")
    public void addTrackTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        String resultValid = "========== CREATING TRACK ==========<pre>Track TRK1001 has been added.";
        String resultInavlid = "========== CREATING TRACK ==========<pre>Track TRK1001 could not be added.";
        assertAll(
                () -> assertEquals(FormController.addTrack("TRK1001", 20, 70, 200,
                        "FRIEND", "ACTIVE", "LINK-A", 1, time), resultValid),
                () -> assertEquals(FormController.addTrack("TRK1001", 20, 70, 200,
                        "FRIEND", "ACTIVE", "", 1, time), resultInavlid)

        );
    }

//    @Test
//    @Tag("FormController")
//    public void getFilteredTracks(){
//        LocalDateTime time1 = LocalDateTime.parse("2026-11-03T12:45:30");
//        LocalDateTime time2 = LocalDateTime.parse("2026-11-03T12:45:30");
//        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
//                "INACTIVE", "LINK-D", 1, time1, time1);
//        Track track2 = new Track("TRK1001", 70, 100, 20, "UNKNOWN",
//                "ACTIVE", "LINK-A", 1, time2, time2);
//        trackRepository.save(track1);
//        trackRepository.save(track2);
//        String resultValid = "TRK1004";
//        String resultInavlid = "Tracks could not be found.";
//        assertAll(
//                () -> assertTrue(FormController.getFilteredTracks(time1, time1, null).contains(resultValid)),
//                () -> assertEquals(FormController.getFilteredTracks(time2, null, "70"), resultInavlid)
//
//        );
//    }

}
