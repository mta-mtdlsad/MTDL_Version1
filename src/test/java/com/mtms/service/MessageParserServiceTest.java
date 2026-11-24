package com.mtms.service;

import com.mtms.domain.Track;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageParserServiceTest {
    @Mock private AlertService alertService;
    @InjectMocks private MessageParserService parserService;

    @Test
    void parsesPlatformTypeFromTenFieldMessage() {
        Track track = parserService.parse("SRC-A|1|TRK0001|LAND_VEHICLE|24.4539|54.3773|200|FRIEND|ACTIVE|2026-11-23T09:00:00");

        assertEquals("LAND_VEHICLE", track.getTrackType());
        assertEquals(24.4539, track.getLatitude());
        assertEquals(54.3773, track.getLongitude());
    }

    @Test
    void malformedMessageIsRejectedAndReported() {
        assertThrows(IllegalArgumentException.class,
                () -> parserService.parse("SRC-A|1|TRK0001|LAND|24.4|54.3|200|FRIEND|ACTIVE"));
        verify(alertService).createAlert(org.mockito.ArgumentMatchers.eq("TRK0001"), any());
    }
}
