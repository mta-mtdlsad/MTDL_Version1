package com.mtms.simulator;

import com.mtms.domain.GeoPath;
import com.mtms.domain.LatLonPoint;
import com.mtms.service.PlatformGeofenceService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RandomPathGeneratorTest {
    @Test
    void allPathPointsStayWithinTheirConfiguredRadiusIncludingFallbacks() {
        PlatformGeofenceService geofence = mock(PlatformGeofenceService.class);
        when(geofence.isAllowedPoint(anyString(), anyDouble(), anyDouble())).thenReturn(true);
        when(geofence.isAllowedSegment(anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(true);
        RandomPathGenerator generator = new RandomPathGenerator(geofence, new Random(42));

        double centerLat = 24.0;
        double centerLon = 54.0;
        double radiusKm = 25.0;
        List<GeoPath> paths = generator.generateRandomPaths(centerLat, centerLon, radiusKm, 4, 12, 15, "AIR");

        assertEquals(4, paths.size());
        for (GeoPath path : paths) {
            assertEquals(12, path.getPoints().size());
            for (LatLonPoint point : path.getPoints()) {
                assertTrue(distanceKm(centerLat, centerLon, point.getLat(), point.getLon()) <= radiusKm + 1e-6);
            }
        }
    }

    @Test
    void refusesToCreateAPathWhenNoPlatformValidStartCanBeFound() {
        PlatformGeofenceService geofence = mock(PlatformGeofenceService.class);
        when(geofence.isAllowedPoint(anyString(), anyDouble(), anyDouble())).thenReturn(false);
        RandomPathGenerator generator = new RandomPathGenerator(geofence, new Random(3));

        assertThrows(IllegalStateException.class,
                () -> generator.generateRandomPaths(24, 54, 20, 1, 4, 4, "LAND"));
    }

    @Test
    void keepsTheCurrentValidPointWhenNoSafeFallbackStepExists() {
        PlatformGeofenceService geofence = mock(PlatformGeofenceService.class);
        when(geofence.isAllowedPoint(anyString(), anyDouble(), anyDouble())).thenReturn(true);
        when(geofence.isAllowedSegment(anyString(), anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(false);
        RandomPathGenerator generator = new RandomPathGenerator(geofence, new Random(7));

        List<GeoPath> paths = generator.generateRandomPaths(24, 54, 20, 1, 4, 4, "LAND");
        List<LatLonPoint> points = paths.get(0).getPoints();

        assertEquals(4, points.size());
        assertTrue(points.stream().allMatch(point -> point.getLat() == points.get(0).getLat()
                && point.getLon() == points.get(0).getLon()));
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * 6371.0088 * Math.asin(Math.sqrt(a));
    }
}
