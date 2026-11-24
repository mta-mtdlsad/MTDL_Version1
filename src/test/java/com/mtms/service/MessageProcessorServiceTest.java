package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.util.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageProcessorServiceTest {
    @Mock private TrackService trackService;
    @Mock private AlertService alertService;
    @Mock private SourceHealthService sourceHealthService;
    @Mock private ValidationService validationService;

    @Test
    void savesAValidTrackAndRecordsAcceptedOutcome() {
        MessageProcessorService processor = processor();
        Track track = validTrack("LAND", 24.4, 54.3);
        when(validationService.validateTrack(track)).thenReturn(List.of());
        when(trackService.getHighestSequence("SRC-A")).thenReturn(0);
        when(trackService.saveTrack(track)).thenReturn(true);

        MessageProcessorService.ProcessingResult result = processor.processDetailed(track);

        assertEquals(MessageProcessorService.Outcome.ACCEPTED, result.outcome());
        verify(trackService).saveTrack(track);
        verify(sourceHealthService).recordOutcome(eq("SRC-A"), any(LocalDateTime.class), eq("ACCEPTED"), eq(1));
        verify(alertService, never()).createAlert(eq(track), any(ValidationResult.class));
    }

    @Test
    void rejectsInvalidGeographicTrackWithoutSavingIt() {
        MessageProcessorService processor = processor();
        Track track = validTrack("LAND_VEHICLE", 24.4, 54.3);
        ValidationResult invalidPosition = new ValidationResult("Track position does not match platform type; expected land", "HIGH");
        when(validationService.validateTrack(track)).thenReturn(List.of(invalidPosition));

        MessageProcessorService.ProcessingResult result = processor.processDetailed(track);

        assertEquals(MessageProcessorService.Outcome.REJECTED, result.outcome());
        verify(alertService).createAlert(track, invalidPosition);
        verify(trackService, never()).saveTrack(track);
        verify(sourceHealthService).recordOutcome(eq("SRC-A"), any(LocalDateTime.class), eq("REJECTED"), eq(1));
    }

    private MessageProcessorService processor() {
        return new MessageProcessorService(trackService, alertService, sourceHealthService, validationService);
    }

    private Track validTrack(String type, double latitude, double longitude) {
        return new Track("SRC-A", 1, "TRK0001", type, latitude, longitude, 200,
                "FRIEND", "ACTIVE", LocalDateTime.now());
    }
}
