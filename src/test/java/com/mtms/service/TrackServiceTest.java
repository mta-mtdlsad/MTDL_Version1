package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {
    @Mock private TrackRepository trackRepository;
    @InjectMocks private TrackService trackService;

    @Test
    void savesTrackWhenSourceSequenceIsNew() {
        Track track = track();
        when(trackRepository.existsBySourceIdAndSequenceNumber("SRC-A", 1)).thenReturn(false);

        assertTrue(trackService.saveTrack(track));
        verify(trackRepository).save(track);
    }

    @Test
    void doesNotSaveDuplicateSourceSequence() {
        Track track = track();
        when(trackRepository.existsBySourceIdAndSequenceNumber("SRC-A", 1)).thenReturn(true);

        assertFalse(trackService.saveTrack(track));
        verify(trackRepository, never()).save(track);
    }

    private Track track() {
        return new Track("SRC-A", 1, "TRK0001", "SEA", 24.4, 54.3,
                200, "FRIEND", "ACTIVE", LocalDateTime.now());
    }
}
