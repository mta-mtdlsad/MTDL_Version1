package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("TrackService Class Test")
@ExtendWith(MockitoExtension.class)
public class TrackServiceTest {

    @Mock
    private TrackRepository track_repo;
    @InjectMocks
    private TrackService trackService;

    @Test
    @Tag("TracksBasicCRUD")
    @DisplayName("Test getAllTracks Functionality")
    public void getAllTracksTest() {
        Track track = new Track();
        track.setTrackNumber("TRK1001");
        when(track_repo.findAll()).thenReturn(List.of(track));
        assertFalse(trackService.getAllTracks().isEmpty());
    }
}
