package com.mtms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformGeofenceServiceTest {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    void matchesLandAndSeaTypesAgainstPolygon() {
        PlatformGeofenceService geofence = new PlatformGeofenceService(landSquare());

        assertTrue(geofence.isAllowedPoint("LAND_VEHICLE", 0, 0));
        assertFalse(geofence.isAllowedPoint("LAND", 2, 2));
        assertTrue(geofence.isAllowedPoint("SHIP", 2, 2));
        assertFalse(geofence.isAllowedPoint("SEA", 0, 0));
    }

    @Test
    void rejectsLandAndSeaSegmentsThatCrossTheCoastline() {
        PlatformGeofenceService geofence = new PlatformGeofenceService(landSquare());

        assertFalse(geofence.isAllowedSegment("LAND", 0, -2, 0, 2));
        assertFalse(geofence.isAllowedSegment("SEA", 0, -2, 0, 2));
        assertTrue(geofence.isAllowedSegment("LAND", 0, -0.8, 0.8, -0.8));
        assertTrue(geofence.isAllowedSegment("SEA", 0, 2, 0.8, 2));
    }

    @Test
    void airCanTravelOverLandAndWaterAndRealMaskLoadsFromResources() {
        PlatformGeofenceService testGeofence = new PlatformGeofenceService(landSquare());
        assertTrue(testGeofence.isAllowedPoint("AIRCRAFT", 0, 0));
        assertTrue(testGeofence.isAllowedSegment("AIR", 0, -2, 0, 2));

        PlatformGeofenceService naturalEarthGeofence = new PlatformGeofenceService(new ObjectMapper());
        assertTrue(naturalEarthGeofence.isLand(24.4539, 54.3773));
        assertFalse(naturalEarthGeofence.isLand(24.45, 53.5));
    }

    private Polygon landSquare() {
        return geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-1, -1), new Coordinate(1, -1), new Coordinate(1, 1),
                new Coordinate(-1, 1), new Coordinate(-1, -1)
        });
    }
}
