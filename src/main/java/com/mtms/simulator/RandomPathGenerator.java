package com.mtms.simulator;

import com.mtms.model.GeoPath;
import com.mtms.model.LatLonPoint;
import com.mtms.service.PlatformGeofenceService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Generates bounded simulator paths that respect land and water boundaries. */
@Component
public class RandomPathGenerator {
    private static final double EARTH_RADIUS_KM = 6371.0088;
    private static final int START_ATTEMPTS = 1000;

    private final PlatformGeofenceService platformGeofenceService;
    private final Random random;

    public RandomPathGenerator(PlatformGeofenceService platformGeofenceService) {
        this(platformGeofenceService, new Random());
    }

    RandomPathGenerator(PlatformGeofenceService platformGeofenceService, Random random) {
        this.platformGeofenceService = platformGeofenceService;
        this.random = random;
    }

    private LatLonPoint destinationPoint(double lat, double lon, double distanceKm, double bearingDeg) {
        double lat1 = Math.toRadians(lat);
        double lon1 = Math.toRadians(lon);
        double bearing = Math.toRadians(bearingDeg);
        double angularDist = distanceKm / EARTH_RADIUS_KM;
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(angularDist)
                + Math.cos(lat1) * Math.sin(angularDist) * Math.cos(bearing));
        double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(angularDist) * Math.cos(lat1),
                Math.cos(angularDist) - Math.sin(lat1) * Math.sin(lat2));
        return new LatLonPoint(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(Math.min(1, a)));
    }

    public List<GeoPath> generateRandomPaths(
            double centerLat,
            double centerLon,
            double radiusKm,
            int numPaths,
            int pointsPerPath,
            double stepKm,
            String pathType) {
        if (radiusKm <= 0 || numPaths <= 0 || pointsPerPath <= 0 || stepKm <= 0) {
            throw new IllegalArgumentException("Radius, path count, point count, and step size must be positive");
        }

        List<GeoPath> paths = new ArrayList<>(numPaths);
        List<LatLonPoint> allExistingPoints = new ArrayList<>();
        double sectorAngle = 360.0 / numPaths;
        double minSeparationKm = 4.0;

        for (int i = 0; i < numPaths; i++) {
            double sectorStart = i * sectorAngle;
            LatLonPoint current = findStartPoint(centerLat, centerLon, radiusKm, sectorStart, sectorAngle, pathType);
            List<LatLonPoint> points = new ArrayList<>(pointsPerPath);
            points.add(current);
            allExistingPoints.add(current);

            double heading = bearingDegrees(new LatLonPoint(centerLat, centerLon), current)
                    + random.nextDouble() * 40.0 - 20.0;
            for (int pointIndex = 1; pointIndex < pointsPerPath; pointIndex++) {
                LatLonPoint chosenNext = null;
                double chosenHeading = heading;

                for (int attempt = 0; attempt < 25; attempt++) {
                    double turn = random.nextDouble() * 70.0 - 35.0;
                    double candidateHeading = (heading + turn + 360.0) % 360.0;
                    double step = stepKm * (0.6 + 0.4 * random.nextDouble());
                    LatLonPoint candidate = destinationPoint(current.getLat(), current.getLon(), step, candidateHeading);
                    if (isValidNextPoint(current, candidate, centerLat, centerLon, radiusKm, pathType)
                            && !isTooCloseToOtherPaths(candidate, allExistingPoints, points, minSeparationKm)) {
                        chosenNext = candidate;
                        chosenHeading = candidateHeading;
                        break;
                    }
                }

                if (chosenNext == null) {
                    chosenNext = findValidFallback(current, heading, centerLat, centerLon, radiusKm, stepKm, pathType,
                            allExistingPoints, points, minSeparationKm);
                    if (chosenNext == null) {
                        // A stationary observation is safer than accepting an invalid fallback coordinate.
                        chosenNext = current;
                    } else {
                        chosenHeading = bearingDegrees(current, chosenNext);
                    }
                }

                current = chosenNext;
                heading = chosenHeading;
                points.add(current);
                allExistingPoints.add(current);
            }
            paths.add(new GeoPath(i + 1, points, pathType));
        }
        return paths;
    }

    private LatLonPoint findStartPoint(
            double centerLat, double centerLon, double radiusKm, double sectorStart, double sectorAngle, String pathType) {
        for (int attempt = 0; attempt < START_ATTEMPTS; attempt++) {
            double bearing = sectorStart + random.nextDouble() * sectorAngle;
            double distance = radiusKm * (0.20 + 0.45 * Math.sqrt(random.nextDouble()));
            LatLonPoint candidate = destinationPoint(centerLat, centerLon, distance, bearing);
            if (haversineKm(centerLat, centerLon, candidate.getLat(), candidate.getLon()) <= radiusKm
                    && platformGeofenceService.isAllowedPoint(pathType, candidate.getLat(), candidate.getLon())) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not find a valid starting point for " + pathType + " inside the configured radius");
    }

    private LatLonPoint findValidFallback(
            LatLonPoint current,
            double heading,
            double centerLat,
            double centerLon,
            double radiusKm,
            double stepKm,
            String pathType,
            List<LatLonPoint> allPoints,
            List<LatLonPoint> currentPathPoints,
            double minSeparationKm) {
        double[] fractions = {0.5, 0.25, 0.1};
        for (double fraction : fractions) {
            for (int angle = 0; angle < 360; angle += 15) {
                double candidateHeading = (heading + angle + 360.0) % 360.0;
                LatLonPoint candidate = destinationPoint(current.getLat(), current.getLon(), stepKm * fraction, candidateHeading);
                if (isValidNextPoint(current, candidate, centerLat, centerLon, radiusKm, pathType)
                        && !isTooCloseToOtherPaths(candidate, allPoints, currentPathPoints, minSeparationKm)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private boolean isValidNextPoint(
            LatLonPoint current,
            LatLonPoint candidate,
            double centerLat,
            double centerLon,
            double radiusKm,
            String pathType) {
        return isSegmentWithinRadius(current, candidate, centerLat, centerLon, radiusKm)
                && platformGeofenceService.isAllowedPoint(pathType, candidate.getLat(), candidate.getLon())
                && platformGeofenceService.isAllowedSegment(
                        pathType, current.getLat(), current.getLon(), candidate.getLat(), candidate.getLon());
    }

    private boolean isSegmentWithinRadius(
            LatLonPoint start, LatLonPoint end, double centerLat, double centerLon, double radiusKm) {
        double segmentLengthKm = haversineKm(start.getLat(), start.getLon(), end.getLat(), end.getLon());
        int checks = Math.max(1, (int) Math.ceil(segmentLengthKm));
        for (int i = 0; i <= checks; i++) {
            LatLonPoint point = interpolateGreatCircle(start, end, (double) i / checks);
            if (haversineKm(centerLat, centerLon, point.getLat(), point.getLon()) > radiusKm) {
                return false;
            }
        }
        return true;
    }

    private LatLonPoint interpolateGreatCircle(LatLonPoint start, LatLonPoint end, double fraction) {
        if (fraction <= 0) return start;
        if (fraction >= 1) return end;
        double phi1 = Math.toRadians(start.getLat());
        double lambda1 = Math.toRadians(start.getLon());
        double phi2 = Math.toRadians(end.getLat());
        double lambda2 = Math.toRadians(end.getLon());
        double delta = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin((phi2 - phi1) / 2), 2)
                        + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin((lambda2 - lambda1) / 2), 2)));
        if (delta == 0) return start;
        double a = Math.sin((1 - fraction) * delta) / Math.sin(delta);
        double b = Math.sin(fraction * delta) / Math.sin(delta);
        double x = a * Math.cos(phi1) * Math.cos(lambda1) + b * Math.cos(phi2) * Math.cos(lambda2);
        double y = a * Math.cos(phi1) * Math.sin(lambda1) + b * Math.cos(phi2) * Math.sin(lambda2);
        double z = a * Math.sin(phi1) + b * Math.sin(phi2);
        double latitude = Math.toDegrees(Math.atan2(z, Math.sqrt(x * x + y * y)));
        double longitude = Math.toDegrees(Math.atan2(y, x));
        return new LatLonPoint(latitude, longitude);
    }

    private double bearingDegrees(LatLonPoint from, LatLonPoint to) {
        double lat1 = Math.toRadians(from.getLat());
        double lat2 = Math.toRadians(to.getLat());
        double deltaLon = Math.toRadians(to.getLon() - from.getLon());
        double y = Math.sin(deltaLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon);
        return (Math.toDegrees(Math.atan2(y, x)) + 360.0) % 360.0;
    }

    private boolean isTooCloseToOtherPaths(
            LatLonPoint candidate, List<LatLonPoint> allPoints, List<LatLonPoint> currentPathPoints, double minKm) {
        for (LatLonPoint existing : allPoints) {
            if (!currentPathPoints.contains(existing)
                    && haversineKm(candidate.getLat(), candidate.getLon(), existing.getLat(), existing.getLon()) < minKm) {
                return true;
            }
        }
        return false;
    }
}
