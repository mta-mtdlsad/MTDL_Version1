package com.mtms.web.rest;

import com.mtms.domain.Track;
import com.mtms.service.TrackService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrackController Class Test")
public class TrackControllerTest {

    @Mock
    private TrackService trackService;

    @InjectMocks
    private TrackController trackController;

    // Runs before every test
    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("Running Test: " + testInfo.getDisplayName());
    }

    // Runs after each test
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("Finished Test: " + testInfo.getDisplayName());
    }

    // Retrieve all tracks via /api/tracks/list
    @Test
    @DisplayName("Test List Tracks")
    void listTracksTest() {
        // Simulate service returning one track
        when(trackService.getAllTracks()).thenReturn(List.of(new Track()));

        // Call controller method
        List<Track> result = trackController.listTracks();

        // Check returned list size
        assertEquals(1, result.size());
    }

    // Search tracks with valid trackNumber parameter
    @Test
    @DisplayName("Test Search Tracks Valid")
    void searchTracksValidTest() {
        // Simulate finding one matching track
        when(trackService.searchTracks("TRK1001")).thenReturn(List.of(new Track()));

        // Call controller method
        ResponseEntity<List<Track>> response = trackController.searchTracks("TRK1001");

        // Check HTTP 200 and body size
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // Return HTTP 400 when search trackNumber is blank
    @Test
    @DisplayName("Test Search Tracks Empty Input")
    void searchTracksEmptyTest() {
        // Call controller with blank input
        ResponseEntity<List<Track>> response = trackController.searchTracks(" ");

        // Check for HTTP 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Create a track and submit it for processing
    @Test
    @DisplayName("Test Create Track")
    void createTrackTest() {
        // Call controller create endpoint
        ResponseEntity<String> response = trackController.createTrack(
                "SRC01", 1, "TRK1001", 25.0, 55.0, 400.0, "FRIEND", "ACTIVE"
        );

        // Check HTTP 200 and verify processTrack was called
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trackService).processTrack(any(Track.class));
    }

    // Delete matching tracks and return HTTP 200
    @Test
    @DisplayName("Test Delete Track Found")
    void deleteTrackFoundTest() {
        // Simulate deleting 2 matching rows
        when(trackService.deleteTracksByNumber("TRK1001")).thenReturn(2);

        // Call controller delete endpoint
        ResponseEntity<String> response = trackController.deleteTrack("TRK1001");

        // Check for HTTP 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Return HTTP 404 when no matching rows exist to delete
    @Test
    @DisplayName("Test Delete Track Not Found")
    void deleteTrackNotFoundTest() {
        // Simulate 0 rows deleted
        when(trackService.deleteTracksByNumber("TRK9999")).thenReturn(0);

        // Call controller delete endpoint
        ResponseEntity<String> response = trackController.deleteTrack("TRK9999");

        // Check for HTTP 404 Not Found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
