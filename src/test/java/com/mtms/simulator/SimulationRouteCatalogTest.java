package com.mtms.simulator;

import com.mtms.domain.TrackType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationRouteCatalogTest {
    private final SimulationRouteCatalog catalog = new SimulationRouteCatalog();

    @Test
    void everyRouteMatchesItsPlatformEnvironmentAndConfiguredCorridor() {
        for (var route : catalog.getRoutes()) {
            assertTrue(SimulationRouteCatalog.environmentMatches(route.platformType(), route.environment()));
            for (int sample = 0; sample < 50; sample++) {
                assertTrue(catalog.isCompatiblePosition(route.platformType(), catalog.positionAt(route, sample, 50)));
            }
        }
    }

    @Test
    void routeSamplingStartsAndEndsAtItsWaypoints() {
        for (TrackType type : new TrackType[]{TrackType.SHIP, TrackType.AIRCRAFT, TrackType.LAND_VEHICLE}) {
            var route = catalog.routeFor(type, 0);
            assertEquals(route.waypoints().get(0), catalog.positionAt(route, 0, 50));
            assertEquals(route.waypoints().get(route.waypoints().size() - 1), catalog.positionAt(route, 49, 50));
        }
    }

    @Test
    void unknownTypeHasNoMovementRoute() {
        assertThrows(IllegalArgumentException.class, () -> catalog.routeFor(TrackType.UNKNOWN, 0));
        assertFalse(catalog.isCompatiblePosition(TrackType.SHIP, new SimulationRouteCatalog.Coordinate(24.45, 54.37)));
        assertFalse(catalog.isCompatiblePosition(TrackType.LAND_VEHICLE, new SimulationRouteCatalog.Coordinate(25.8, 53.4)));
    }
}
