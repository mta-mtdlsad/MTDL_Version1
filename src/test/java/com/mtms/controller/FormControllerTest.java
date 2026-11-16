package com.mtms.controller;

import com.mtms.domain.Track;
import com.mtms.main.MTMSApplication;
import com.mtms.repository.TrackRepository;
import com.mtms.service.TrackService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MTMSApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FormControllerTest {

    @Autowired
    private FormController formController;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private TrackService trackService;


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
                () -> assertEquals(formController.addTrack("TRK1001", 20, 70, 200,
                        "FRIEND", "ACTIVE", "LINK-A", 1, time), resultValid),
                () -> assertEquals(formController.addTrack("TRK1001", 20, 70, 200,
                        "FRIEND", "ACTIVE", "", 1, time), resultInavlid)

        );
    }

    @Test
    @Tag("FormController")
    public void getReplayTracksTest(){
        LocalDateTime time1 = LocalDateTime.parse("2026-11-03T12:45:30");
        LocalDateTime time2 = LocalDateTime.parse("2026-11-03T12:45:40");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time1, time1);
        Track track2 = new Track("TRK1001", 70, 100, 20, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time2, time2);
        trackRepository.save(track1);
        trackRepository.save(track2);
        String resultInavlid = "Tracks could not be found.";
        assertAll(
                () -> assertTrue(formController.getReplayTracks(time1, time1, "half").contains("TRK1004")),
                () -> assertFalse(formController.getReplayTracks(time1, time1, "half").contains("TRK1001")),
                () -> assertEquals(formController.getReplayTracks(time2, null, ""), resultInavlid)

        );
    }

    @Test
    @Tag("FormController")
    public void getCurrentTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1001", 70, 100, 20, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time, time);

        String resultInavlid = "========== LIST OF CURRENT TRACKS ==========<pre>No tracks available.";
        assertEquals(formController.getCurrentTracks(), resultInavlid);

        trackRepository.save(track1);
        trackRepository.save(track2);

        assertAll(
                () -> assertTrue(formController.getCurrentTracks().contains("TRK1004")),
                () -> assertTrue(formController.getCurrentTracks().contains("TRK1001"))
        );
    }

    @Test
    @Tag("FormController")
    public void getTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1001", 70, 100, 20, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time, time);

        String resultInavlid = "========== LIST OF ALL TRACKS ==========<pre>No tracks available.";
        assertEquals(formController.getTracks(), resultInavlid);

        trackRepository.save(track1);
        trackRepository.save(track2);

        assertAll(
                () -> assertTrue(formController.getTracks().contains("TRK1004")),
                () -> assertTrue(formController.getTracks().contains("TRK1001"))
        );
    }

    @Test
    @Tag("FormController")
    public void getFilteredTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1001", 70, 100, 20, "UNKNOWN",
                "ACTIVE", "LINK-A", 1, time, time);

        String resultInavlid = "Tracks could not be found.";

        trackRepository.save(track1);
        trackRepository.save(track2);

        assertAll(
                () -> assertTrue(formController.getFilteredTracks("HOSTILE", "", "").contains("TRK1004")),
                () -> assertFalse(formController.getFilteredTracks("HOSTILE", "", "").contains("TRK1001")),
                () -> assertEquals(formController.getFilteredTracks("", "", "500"), resultInavlid)
        );
    }

    @Test
    @Tag("FormController")
    public void getAdvanceFilteredTracksTest(){
        LocalDateTime time = LocalDateTime.parse("2026-11-03T12:45:30");
        Track track1= new Track("TRK1004", 10, 50, 200, "HOSTILE",
                "INACTIVE", "LINK-D", 1, time, time);
        Track track2 = new Track("TRK1001", 70, 100, 20, "UNKNOWN",
                "ACTIVE", "LINK-D", 1, time, time);

        String resultInavlid = "Tracks could not be found.";

        trackRepository.save(track1);
        trackRepository.save(track2);

        assertAll(
                () -> assertTrue(formController.getAdvanceFilteredTracks("LINK-D", "", "", "INACTIVE", "100", "", "", "").contains("TRK1004")),
                () -> assertFalse(formController.getAdvanceFilteredTracks("LINK-D", "", "", "INACTIVE", "100", "", "", "").contains("TRK1001")),
                () -> assertEquals(formController.getAdvanceFilteredTracks("LINK-A", "", "HOSTILE", "ACTIVE", "", "", "", ""), resultInavlid)
        );
    }

    @Test
    @Tag("FormController")
    @DisplayName("Testing Delete Track By Number")
    public void testDeleteTrackByNum() {

        //create 1 valid track (will be saved to the database) and 1 invalid track (that will not exist in database) to be deleted
        String track1IdValid = "TRK1001";
        String track2IdInvalid = "0";


        //set valid track number and parameters, and save to track repository
        Track track1 = new Track();
        track1.setTrackNumber(track1IdValid);
        trackRepository.save(track1);

        // save invalid track number and parameters
        Track track2 = new Track();
        track2.setTrackNumber(track2IdInvalid);

        //call method to delete valid track (should return success)
        boolean validResult = trackService.deleteTrackByNum(track1IdValid);

        //call method to delete invalid track (should return error)
        boolean invalidResult = trackService.deleteTrackByNum(track2IdInvalid);

        //check whether the 2 inputs (valid/invalid) are present in DB
        List<Track> deleteValidTrack = trackRepository.findByTrackNumber(track1IdValid);
        List<Track> deleteInvalidTrack = trackRepository.findByTrackNumber(track2IdInvalid);


        assertAll(
                () -> assertTrue(validResult, "Valid track should be deleted successfully"),
                () -> assertTrue(deleteValidTrack.isEmpty(), "Deleted track should not be in database"),
                () -> assertFalse(invalidResult, "Deleting an invalid track should return false"),
                () -> assertTrue(deleteInvalidTrack.isEmpty(), "Invalid track should not be in database")
        );

    }

    @Test
    @Tag("FormController")
    @DisplayName("Testing Get Tracks By Valid/Invalid Track Number")
    public void testSearchTracks() {

        //create 2 valid tracks and 1 invalid track number
        String track1IdValid = "TRK1001";
        String track2IdValid = "TRK1002";
        String trackIdInvalid = "0";

        //create first valid track
        Track track1 = new Track();
        track1.setTrackNumber(track1IdValid);
        track1.setLatitude(19.971600);
        track1.setLongitude(87.584600);
        track1.setSpeed(780);
        track1.setIdentity("FRIEND");
        track1.setTrackStatus("INACTIVE");
        track1.setSourceId("LINK-A");
        track1.setSequenceNumber(1);
        track1.setObservationTime(LocalDateTime.parse("2026-11-02T09:30:00"));
        track1.setReceivedTime(LocalDateTime.parse("2026-11-02T11:30:00"));
        trackRepository.save(track1);

        //create second valid track
        Track track2 = new Track();
        track2.setTrackNumber(track2IdValid);
        track2.setLatitude(12.971600);
        track2.setLongitude(77.594600);
        track2.setSpeed(750);
        track2.setIdentity("FRIEND");
        track2.setTrackStatus("ACTIVE");
        track2.setSourceId("LINK-B");
        track2.setSequenceNumber(2);
        track2.setObservationTime(LocalDateTime.parse("2026-11-02T08:30:00"));
        track2.setReceivedTime(LocalDateTime.parse("2026-11-02T10:30:00"));
        trackRepository.save(track2);

        //call method on valid and invalid track numbers
        List<Track> valid1Result = trackService.findTrackByNum(track1IdValid);
        List<Track> valid2Result = trackService.findTrackByNum(track2IdValid);
        List<Track> invalidResult = trackService.findTrackByNum(trackIdInvalid);

        assertAll(
                () -> assertNotNull(valid1Result, "Valid result list should not be null"),
                () -> assertFalse(valid1Result.isEmpty(), "Expected valid track to be found"),
                () -> assertEquals(track1IdValid, valid1Result.get(0).getTrackNumber(), "Track number should match"),
                () -> assertNotNull(valid2Result, "Valid result list should not be null"),
                () -> assertFalse(valid2Result.isEmpty(), "Expected valid track to be found"),
                () -> assertEquals(track2IdValid, valid2Result.get(0).getTrackNumber(), "Track number should match"),
                () -> assertNotNull(invalidResult, "Invalid result list should not be null"),
                () -> assertTrue(invalidResult.isEmpty(), "Expected empty list when track number does not exist")
        );
    }

    @Test
    @Tag("FormController")
    @DisplayName("Testing Get Full History of Track")
    public void testGetFullHistory() {

        //create 2 valid tracks and 1 invalid track number
        String trackNum = "TRK1001";
        String sourceId = "LINK-A";


        //create first track
        Track track1 = new Track();
        track1.setTrackNumber(trackNum);
        track1.setLatitude(19.971600);
        track1.setLongitude(87.584600);
        track1.setSpeed(780);
        track1.setIdentity("FRIEND");
        track1.setTrackStatus("ACTIVE");
        track1.setSourceId(sourceId);
        track1.setSequenceNumber(1);
        track1.setObservationTime(LocalDateTime.parse("2026-11-02T09:30:00"));
        track1.setReceivedTime(LocalDateTime.parse("2026-11-02T11:30:00"));
        trackRepository.save(track1);

        //create second track
        Track track2 = new Track();
        track2.setTrackNumber(trackNum);
        track2.setLatitude(12.971600);
        track2.setLongitude(77.594600);
        track2.setSpeed(750);
        track2.setIdentity("FRIEND");
        track2.setTrackStatus("ACTIVE");
        track2.setSourceId(sourceId);
        track2.setSequenceNumber(2);
        track2.setObservationTime(LocalDateTime.parse("2026-11-02T10:30:00"));
        track2.setReceivedTime(LocalDateTime.parse("2026-11-02T13:30:00"));
        trackRepository.save(track2);

        //call method
        List<Track> result = trackService.getFullHistory(trackNum, sourceId);


        assertAll(
                () -> assertNotNull(result, "History result should not be null"),
                () -> assertFalse(result.isEmpty(), "Expected list of tracks to be found"),
                () -> assertEquals(trackNum, result.get(0).getTrackNumber(), "Track number should match"),
                () -> assertEquals(sourceId, result.get(0).getSourceId(), "Source ID should match")
        );
    }

    @Test
    @Tag("Track")
    @DisplayName("Testing Get Full History of Track Empty List")
    public void negativeGetFullHistory(TestReporter Tr) {

        //use non existing track and source ID
        String trackNum = "TRK000";
        String sourceId = "LINK-X";

        //create empty list and call method
        List<Track> result = trackService.getFullHistory(trackNum, sourceId);

        //checks list is not null, and is empty then prints message
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected the list of tracks to be empty");

        //print total size of tracks retrieved (should equal to zero)
        Tr.publishEntry("Total tracks found: " + result.size());
    }


}
