package com.mtms.simulator;

import com.mtms.domain.TrackType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Fixed synthetic corridors used by the demo simulator. Replace with client GIS/routes for authoritative geofencing. */
public final class SimulationRouteCatalog {
    public enum Environment { SEA, AIR, LAND }

    public record Coordinate(double latitude, double longitude) {
        public Coordinate {
            if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                    || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Route coordinate must be a finite WGS 84 position.");
            }
        }
    }

    public record Route(String routeId, TrackType platformType, Environment environment,
                        List<Coordinate> waypoints) {
        public Route {
            Objects.requireNonNull(routeId, "routeId");
            Objects.requireNonNull(platformType, "platformType");
            Objects.requireNonNull(environment, "environment");
            waypoints = List.copyOf(waypoints);
            if (waypoints.size() < 2) throw new IllegalArgumentException("Route requires at least two waypoints.");
            if (!environmentMatches(platformType, environment)) {
                throw new IllegalArgumentException("Route environment does not match its platform type.");
            }
            if (waypoints.stream().anyMatch(point -> !insideDemoCorridor(platformType, point))) {
                throw new IllegalArgumentException("Route waypoint is outside the configured demo corridor for " + platformType + ".");
            }
        }
    }

    private final List<Route> routes = List.of(
            route("SEA-GULF-1", TrackType.SHIP, Environment.SEA,
                    new double[][]{{26.0, 53.0}, {25.8, 53.4}, {25.6, 53.8}, {25.4, 54.1}}),
            route("SEA-GULF-2", TrackType.SHIP, Environment.SEA,
                    new double[][]{{25.8, 52.4}, {25.6, 52.9}, {25.4, 53.4}, {25.2, 53.9}}),
            route("AIR-UAE-1", TrackType.AIRCRAFT, Environment.AIR,
                    new double[][]{{24.4539, 54.3773}, {24.70, 54.78}, {24.95, 55.03}, {25.2048, 55.2708}}),
            route("AIR-UAE-2", TrackType.AIRCRAFT, Environment.AIR,
                    new double[][]{{25.2048, 55.2708}, {24.95, 55.03}, {24.70, 54.78}, {24.4539, 54.3773}}),
            route("LAND-UAE-1", TrackType.LAND_VEHICLE, Environment.LAND,
                    new double[][]{{24.4539, 54.3773}, {24.62, 54.66}, {24.82, 54.94}, {25.02, 55.12}, {25.2048, 55.2708}}),
            route("LAND-UAE-2", TrackType.LAND_VEHICLE, Environment.LAND,
                    new double[][]{{25.2048, 55.2708}, {25.02, 55.12}, {24.82, 54.94}, {24.62, 54.66}, {24.4539, 54.3773}})
    );

    public List<Route> getRoutes() { return routes; }

    public List<Route> routesFor(TrackType type) {
        TrackType normalized = type == null ? TrackType.UNKNOWN : type;
        return routes.stream().filter(route -> route.platformType() == normalized).toList();
    }

    public Route routeFor(TrackType type, int stableTrackIndex) {
        List<Route> compatible = routesFor(type);
        if (compatible.isEmpty()) throw new IllegalArgumentException("No route configured for " + type + ".");
        return compatible.get(Math.floorMod(stableTrackIndex, compatible.size()));
    }

    /** Samples the whole route by distance, including both endpoints. */
    public Coordinate positionAt(Route route, int sampleIndex, int sampleCount) {
        Objects.requireNonNull(route, "route");
        if (sampleCount < 2) throw new IllegalArgumentException("sampleCount must be at least two.");
        double progress = Math.max(0, Math.min(1, (double) sampleIndex / (sampleCount - 1)));
        List<Coordinate> points = route.waypoints();
        double[] lengths = new double[points.size() - 1];
        double total = 0;
        for (int i = 0; i < lengths.length; i++) total += lengths[i] = distance(points.get(i), points.get(i + 1));
        if (total == 0) return points.get(0);
        double target = total * progress, traversed = 0;
        for (int i = 0; i < lengths.length; i++) {
            if (target <= traversed + lengths[i] || i == lengths.length - 1) {
                double fraction = lengths[i] == 0 ? 0 : (target - traversed) / lengths[i];
                Coordinate a = points.get(i), b = points.get(i + 1);
                Coordinate position = new Coordinate(a.latitude() + (b.latitude() - a.latitude()) * fraction,
                        a.longitude() + (b.longitude() - a.longitude()) * fraction);
                if (!isCompatiblePosition(route.platformType(), position)) {
                    throw new IllegalStateException("Generated position left the configured platform corridor.");
                }
                return position;
            }
            traversed += lengths[i];
        }
        return points.get(points.size() - 1);
    }

    public boolean isCompatiblePosition(TrackType type, Coordinate point) {
        return type != null && point != null && insideDemoCorridor(type, point);
    }

    public static boolean environmentMatches(TrackType type, Environment environment) {
        if (type == null || environment == null) return false;
        return switch (type) {
            case SHIP -> environment == Environment.SEA;
            case AIRCRAFT -> environment == Environment.AIR;
            case LAND_VEHICLE -> environment == Environment.LAND;
            case UNKNOWN -> false;
        };
    }

    private static boolean insideDemoCorridor(TrackType type, Coordinate point) {
        return switch (type) {
            case SHIP -> point.latitude() >= 25.0 && point.latitude() <= 26.5
                    && point.longitude() >= 51.5 && point.longitude() <= 54.2;
            case LAND_VEHICLE -> point.latitude() >= 24.0 && point.latitude() <= 25.6
                    && point.longitude() >= 54.2 && point.longitude() <= 55.5;
            case AIRCRAFT -> point.latitude() >= 24.0 && point.latitude() <= 26.0
                    && point.longitude() >= 52.0 && point.longitude() <= 57.0;
            case UNKNOWN -> false;
        };
    }

    private static Route route(String id, TrackType type, Environment environment, double[][] values) {
        List<Coordinate> waypoints = Arrays.stream(values).map(p -> new Coordinate(p[0], p[1])).toList();
        return new Route(id, type, environment, waypoints);
    }

    private static double distance(Coordinate a, Coordinate b) {
        double lat = Math.toRadians(b.latitude() - a.latitude());
        double lon = Math.toRadians(b.longitude() - a.longitude());
        double mean = Math.toRadians((a.latitude() + b.latitude()) / 2.0);
        return Math.hypot(lat * 6371.0, lon * 6371.0 * Math.cos(mean));
    }
}
