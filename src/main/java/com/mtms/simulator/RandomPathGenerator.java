package com.mtms.simulator;

import com.mtms.model.GeoPath;
import com.mtms.model.LatLonPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPathGenerator {
    private final double EARTH_RADIUS_KM = 6371.0;
    private final Random random = new Random();

    private LatLonPoint destinationPoint(double lat, double lon, double distanceKm, double bearingDeg) {
        double lat1 = Math.toRadians(lat);
        double lon1 = Math.toRadians(lon);
        double bearing = Math.toRadians(bearingDeg);
        double angularDist = distanceKm / EARTH_RADIUS_KM;

        double lat2 = Math.asin(
                Math.sin(lat1) * Math.cos(angularDist) +
                        Math.cos(lat1) * Math.sin(angularDist) * Math.cos(bearing)
        );
        double lon2 = lon1 + Math.atan2(
                Math.sin(bearing) * Math.sin(angularDist) * Math.cos(lat1),
                Math.cos(angularDist) - Math.sin(lat1) * Math.sin(lat2)
        );

        return new LatLonPoint(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(a));
    }

    public List<GeoPath> generateRandomPaths(
            double centerLat, double centerLon, double radiusKm,
            int numPaths, int pointsPerPath, double stepKm, String pathType) {

        List<GeoPath> paths = new ArrayList<>(numPaths);
        List<LatLonPoint> allExistingPoints = new ArrayList<>();

        double sectorAngle = 360.0 / numPaths;
        double minSeparationKm = 4.0;

        for (int i = 0; i < numPaths; i++) {
            List<LatLonPoint> points = new ArrayList<>(pointsPerPath);

            // Start inside a dedicated angular sector
            double sectorStart = i * sectorAngle;
            double startBearing = sectorStart + (random.nextDouble() * sectorAngle * 0.8);
            double startDist = radiusKm * (0.20 + 0.45 * Math.sqrt(random.nextDouble()));

            LatLonPoint current = destinationPoint(centerLat, centerLon, startDist, startBearing);
            points.add(current);
            allExistingPoints.add(current);

            double heading = startBearing + (random.nextDouble() * 40.0 - 20.0);

            for (int p = 1; p < pointsPerPath; p++) {
                LatLonPoint chosenNext = null;
                double chosenHeading = heading;

                for (int attempt = 0; attempt < 25; attempt++) {
                    double turn = (random.nextDouble() * 70.0) - 35.0;
                    double candidateHeading = (heading + turn + 360.0) % 360.0;
                    double step = stepKm * (0.6 + 0.4 * random.nextDouble());

                    LatLonPoint candidate = destinationPoint(current.getLat(), current.getLon(), step, candidateHeading);

                    if (haversineKm(centerLat, centerLon, candidate.getLat(), candidate.getLon()) > radiusKm) {
                        heading = (heading + 120.0) % 360.0;
                        continue;
                    }

                    if (!isTooCloseToOtherPaths(candidate, allExistingPoints, points, minSeparationKm)) {
                        chosenNext = candidate;
                        chosenHeading = candidateHeading;
                        break;
                    }
                }

                if (chosenNext == null) {
                    double fallbackHeading = (heading + 90.0) % 360.0;
                    chosenNext = destinationPoint(current.getLat(), current.getLon(), stepKm * 0.5, fallbackHeading);
                    chosenHeading = fallbackHeading;
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

    private boolean isTooCloseToOtherPaths(
            LatLonPoint candidate, List<LatLonPoint> allPoints, List<LatLonPoint> currentPathPoints, double minKm) {
        for (LatLonPoint existing : allPoints) {
            if (!currentPathPoints.contains(existing)) {
                if (haversineKm(candidate.getLat(), candidate.getLon(), existing.getLat(), existing.getLon()) < minKm) {
                    return true;
                }
            }
        }
        return false;
    }
}
