package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.main.MTMSApplication;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = MTMSApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TrackAnalyticsServiceTest{

    @Autowired
    private TrackAnalyticsService trackAnalyticsService;

    @Autowired
    private TrackRepository trackRepository;

    @BeforeEach
    public void clearRepository(){
        trackRepository.deleteAll();
    }

    @Test
    @Tag("TrackAnalytics")
    public void showReportStatTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1 = new Track( "TRK1008", 10, 50, 200, "UNKNOWN",
                "ACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track( "TRK1008", 10, 50, 800, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time, time);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<String> stringList = Arrays.asList(
                "Hostile Tracks:",
                "Track{trackId=12, trackNumber='TRK1008', latitude=10.0, longitude=50.0, speed=800.0, identity='HOSTILE', trackStatus='INACTIVE', sourceId='LINK-D', sequenceNumber=2, observationTime=2026-11-03T12:45:30, receivedTime=2026-11-03T12:45:30}",
                "------------------------<pre>",
                "High Speed Tracks:",
                "Track{trackId=12, trackNumber='TRK1008', latitude=10.0, longitude=50.0, speed=800.0, identity='HOSTILE', trackStatus='INACTIVE', sourceId='LINK-D', sequenceNumber=2, observationTime=2026-11-03T12:45:30, receivedTime=2026-11-03T12:45:30}",
                "------------------------<pre>",
                "Active Tracks:",
                "Track{trackId=11, trackNumber='TRK1008', latitude=10.0, longitude=50.0, speed=200.0, identity='UNKNOWN', trackStatus='ACTIVE', sourceId='LINK-D', sequenceNumber=1, observationTime=2026-11-03T12:45:30, receivedTime=2026-11-03T12:45:30}",
                "------------------------<pre>",
                "Inactive Tracks:",
                "Track{trackId=12, trackNumber='TRK1008', latitude=10.0, longitude=50.0, speed=800.0, identity='HOSTILE', trackStatus='INACTIVE', sourceId='LINK-D', sequenceNumber=2, observationTime=2026-11-03T12:45:30, receivedTime=2026-11-03T12:45:30}",
                "------------------------<pre>",
                "Identity Count:",
                "HOSTILE: 1",
                "UNKNOWN: 1"
        );
        assertAll(
                () -> assertThat(trackAnalyticsService.showReportStat()).isEqualTo(stringList),
                () -> assertThat(trackAnalyticsService.showReportStat()).isNotNull()
        );
    }

    @Test
    @Tag("TrackAnalytics")
    public void showHostileTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1 = new Track("TRK1004", 10, 50, 200, "UNKNOWN",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time, time);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<String> stringList = Arrays.asList(track2.toString());
        List<String> errorMessage = Arrays.asList("No hostile tracks available");
        assertAll(
                () -> assertThat(trackAnalyticsService.showHostileTracks()).isEqualTo(stringList),
                () -> assertThat(trackAnalyticsService.showHostileTracks()).isNotEqualTo(errorMessage)
        );
    }

    @Test
    @Tag("TrackAnalytics")
    public void showHighSpeedTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1 = new Track("TRK1004", 10, 50, 200, "UNKNOWN",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 10, 50, 800, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time, time);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<String> stringList = Arrays.asList(track2.toString());
        List<String> errorMessage = Arrays.asList("No available tracks with high speed");
        assertAll(
                () -> assertThat(trackAnalyticsService.showHighSpeedTracks()).isEqualTo(stringList),
                () -> assertThat(trackAnalyticsService.showHighSpeedTracks()).isNotEqualTo(errorMessage)
        );
    }

    @Test
    @Tag("TrackAnalytics")
    public void showIdentityCountTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1 = new Track("TRK1004", 10, 50, 200, "UNKNOWN",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 10, 50, 800, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time, time);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<String> stringList = Arrays.asList("HOSTILE: 1", "UNKNOWN: 1");
        assertAll(
                () -> assertThat(trackAnalyticsService.showIdentityCount()).isEqualTo(stringList),
                () -> assertThat(trackAnalyticsService.showIdentityCount()).isNotNull()
        );
    }

    @Test
    @Tag("TrackAnalytics")
    public void showActiveTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1 = new Track("TRK1004", 10, 50, 200, "UNKNOWN",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 10, 50, 800, "HOSTILE",
                "ACTIVE", "LINK-D", 2, time, time);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<String> stringList = Arrays.asList(track2.toString());
        List<String> errorMessage = Arrays.asList("No available active tracks");
        assertAll(
                () -> assertThat(trackAnalyticsService.showActiveTracks()).isEqualTo(stringList),
                () -> assertThat(trackAnalyticsService.showActiveTracks()).isNotEqualTo(errorMessage)
        );
    }

    @Test
    @Tag("TrackAnalytics")
    public void showInactiveTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1 = new Track("TRK1004", 10, 50, 200, "UNKNOWN",
                "ACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1004", 10, 50, 800, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time, time);
        trackRepository.save(track1);
        trackRepository.save(track2);
        List<String> stringList = Arrays.asList(track2.toString());
        List<String> errorMessage = Arrays.asList("No available inactive tracks");
        assertAll(
                () -> assertThat(trackAnalyticsService.showInactiveTracks()).isEqualTo(stringList),
                () -> assertThat(trackAnalyticsService.showInactiveTracks()).isNotEqualTo(errorMessage)
        );
    }

    @Test
    @Tag("TrackAnalytics")
    public void getCorrelatedTracksTest(){
        LocalDateTime time1 = LocalDateTime.parse("2026-11-03T12:45:30");
        LocalDateTime time2 = LocalDateTime.parse("2026-11-03T12:45:40");
        Track track1 = new Track("TRK1004", 10, 50, 200, "UNKNOWN",
                "ACTIVE", "LINK-D", 1, time1, time1);
        Track track2 = new Track("TRK1004", 10, 50, 250, "UNKNOWN",
                "ACTIVE", "LINK-A", 2, time2, time2);
        Track track3 = new Track("TRK1005", 10, 50, 800, "HOSTILE",
                "INACTIVE", "LINK-D", 2, time1, time1);
        trackRepository.save(track1);
        trackRepository.save(track2);
        trackRepository.save(track3);
        List<String> stringList = Arrays.asList(
                        "\nTracks SourceId: LINK-D, LINK-A" +
                        "\nTrack Number: TRK1004" +
                        "\nFirst Track Position: 10.0, 50.0" +
                        "\nSecond Track Position: 10.0, 50.0" +
                        "\nTime Difference: 10 seconds" +
                        "\nSpeed Difference: 50.0" +
                        "\nDistance: 0.0 km");
        assertAll(
                () -> assertThat(trackAnalyticsService.getCorrelatedTracks()).isEqualTo(stringList),
                () -> assertThat(trackAnalyticsService.getCorrelatedTracks()).isNotNull()
        );
    }

    @Test
    @Tag("TrackAnalytics")
    public void calculateDistanceTest(){
        assertAll(
                () -> assertThat(trackAnalyticsService.calculateDistance(10.0, 30.0 , 50.0, 80.0)).isEqualTo(6554.844254246136),
                () -> assertThat(trackAnalyticsService.calculateDistance(0.0, 30.0 , 0.0, 10.0)).isNotEqualTo(0.0)
        );
    }



}