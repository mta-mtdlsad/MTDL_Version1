package com.mtms.service;

import com.mtms.model.Track;
import com.mtms.util.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {
    @Mock
    private PlatformGeofenceService geofence;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService(geofence);
    }

    @Test
    void rejectsLandVehiclePointOutsideLand() {
        Track track = track("LAND_VEHICLE", 24.4, 54.3);
        when(geofence.isAllowedPoint("LAND_VEHICLE", 24.4, 54.3)).thenReturn(false);

        List<ValidationResult> errors = validationService.validateTrack(track);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).getMessage().contains("expected land"));
        verify(geofence).isAllowedPoint("LAND_VEHICLE", 24.4, 54.3);
    }

    @Test
    void rejectsShipPointOnLand() {
        Track track = track("SEA", 24.4, 54.3);
        when(geofence.isAllowedPoint("SEA", 24.4, 54.3)).thenReturn(false);

        List<ValidationResult> errors = validationService.validateTrack(track);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).getMessage().contains("expected water"));
    }

    @Test
    void doesNotApplyLandOrSeaRuleToAircraftOrUnknownPlatform() {
        assertTrue(validationService.validateTrack(track("AIRCRAFT", 24.4, 54.3)).isEmpty());
        assertTrue(validationService.validateTrack(track("UNKNOWN", 24.4, 54.3)).isEmpty());
        verify(geofence, never()).isAllowedPoint(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyDouble(), org.mockito.ArgumentMatchers.anyDouble());
    }

    @Test
    void retainsNumericRangeChecks() {
        Track track = track("LAND", 91, 54.3);
        List<ValidationResult> errors = validationService.validateTrack(track);

        assertTrue(errors.stream().anyMatch(error -> error.getMessage().contains("Latitude must")));
    }

    private Track track(String type, double latitude, double longitude) {
        return new Track("SRC-A", 1, "TRK0001", type, latitude, longitude, 200,
                "FRIEND", "ACTIVE", LocalDateTime.now());
    }
}
