package com.mtms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Applies platform-specific geographic checks against the bundled Natural Earth land mask.
 * Coordinates are WGS84 longitude/latitude as used by the UDP track messages.
 */
@Service
public class PlatformGeofenceService {
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private static final double EARTH_RADIUS_KM = 6371.0088;
    private static final double MAX_SEGMENT_CHECK_INTERVAL_KM = 1.0;

    private final PreparedGeometry landGeometry;

    /*
     * Initializes the service by loading and indexing the Natural Earth land mask GeoJSON file.
     *
     * @param objectMapper Jackson ObjectMapper used to parse the GeoJSON resource.
     */
    @Autowired
    public PlatformGeofenceService(ObjectMapper objectMapper) {
        this.landGeometry = loadLandGeometry(objectMapper);
    }

    /*
     * Package-private constructor for unit testing with a custom JTS
     *      Geometry instead of loading the full GeoJSON.
     *
     * @param landGeometry The JTS Geometry representing land areas.
     */
    PlatformGeofenceService(Geometry landGeometry) {
        this.landGeometry = PreparedGeometryFactory.prepare(landGeometry);
    }

    /*
     * Checks whether a given GPS coordinate falls on land (including coastlines).
     *
     * @param latitude  Latitude in decimal degrees.
     * @param longitude Longitude in decimal degrees.
     * @return true if the coordinate is on land or a coastline, false if over water.
     */
    public boolean isLand(double latitude, double longitude) {
        Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
        // covers() intentionally treats a coastline boundary as land.
        return landGeometry.covers(point);
    }

    /*
     * Verifies whether a coordinate is geographically valid for the specified platform type
     * (LAND requires land, SEA requires water, AIR and UNKNOWN are unrestricted).
     *
     * @param trackType The platform/track type string ("LAND", "SEA", "AIR").
     * @param latitude  Latitude in decimal degrees.
     * @param longitude Longitude in decimal degrees.
     * @return true if the position is valid for the platform type, false otherwise.
     */
    public boolean isAllowedPoint(String trackType, double latitude, double longitude) {
        return switch (platformKind(trackType)) {
            case LAND -> isLand(latitude, longitude);
            case SEA -> !isLand(latitude, longitude);
            case AIR, UNKNOWN -> true;
        };
    }

    /* Validates an entire great-circle route segment between
     *      two coordinates by sampling points at 1 km intervals.
     *
     * @param trackType      The platform/track type string.
     * @param startLatitude  Starting latitude in decimal degrees.
     * @param startLongitude Starting longitude in decimal degrees.
     * @param endLatitude    Ending latitude in decimal degrees.
     * @param endLongitude   Ending longitude in decimal degrees.
     * @return true if all sampled points along the route are valid
     *      for the platform type, false if any point violates the geofence.
     */
    public boolean isAllowedSegment(
            String trackType,
            double startLatitude,
            double startLongitude,
            double endLatitude,
            double endLongitude) {
        PlatformKind kind = platformKind(trackType);
        if (kind == PlatformKind.AIR || kind == PlatformKind.UNKNOWN) {
            return true;
        }

        double distanceKm = haversineKm(startLatitude, startLongitude, endLatitude, endLongitude);
        int checks = Math.max(1, (int) Math.ceil(distanceKm / MAX_SEGMENT_CHECK_INTERVAL_KM));
        for (int i = 0; i <= checks; i++) {
            double fraction = (double) i / checks;
            Coordinate point = interpolateGreatCircle(
                    startLatitude, startLongitude, endLatitude, endLongitude, fraction);
            if (!isAllowedPoint(trackType, point.y, point.x)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Reads 'geodata/ne_10m_land.geojson' from the classpath and
     *      builds an indexed JTS MultiPolygon.
     *
     * @param objectMapper Jackson ObjectMapper to parse JSON nodes.
     * @return A PreparedGeometry containing all land polygons for fast spatial lookups.
     */
    private PreparedGeometry loadLandGeometry(ObjectMapper objectMapper) {
        ClassPathResource resource = new ClassPathResource("geodata/ne_10m_land.geojson");
        try (InputStream input = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(input);
            List<Polygon> polygons = new ArrayList<>();
            for (JsonNode feature : root.path("features")) {
                JsonNode geometry = feature.path("geometry");
                String type = geometry.path("type").asText();
                JsonNode coordinates = geometry.path("coordinates");
                if ("Polygon".equals(type)) {
                    polygons.add(createPolygon(coordinates));
                } else if ("MultiPolygon".equals(type)) {
                    for (JsonNode polygonCoordinates : coordinates) {
                        polygons.add(createPolygon(polygonCoordinates));
                    }
                }
            }
            if (polygons.isEmpty()) {
                throw new IllegalStateException("The Natural Earth land mask contains no polygons");
            }
            MultiPolygon allLand = GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(Polygon[]::new));
            return PreparedGeometryFactory.prepare(allLand);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Unable to load geographic land mask", exception);
        }
    }

    /*
     * Converts a GeoJSON polygon coordinate array
     *      (outer shell + optional interior holes) into a JTS Polygon.
     *
     * @param ringsNode JsonNode array representing polygon rings.
     * @return A constructed JTS Polygon.
     */
    private Polygon createPolygon(JsonNode ringsNode) {
        if (!ringsNode.isArray() || ringsNode.isEmpty()) {
            throw new IllegalStateException("A land polygon has no boundary rings");
        }
        LinearRing shell = createRing(ringsNode.get(0));
        LinearRing[] holes = new LinearRing[Math.max(0, ringsNode.size() - 1)];
        for (int i = 1; i < ringsNode.size(); i++) {
            holes[i - 1] = createRing(ringsNode.get(i));
        }
        return GEOMETRY_FACTORY.createPolygon(shell, holes);
    }

    /*
     * Converts a single GeoJSON coordinate array into a closed JTS LinearRing.
     *
     * @param coordinatesNode JsonNode array of [longitude, latitude] pairs.
     * @return A closed JTS LinearRing.
     */
    private LinearRing createRing(JsonNode coordinatesNode) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (JsonNode coordinateNode : coordinatesNode) {
            coordinates.add(new Coordinate(coordinateNode.get(0).asDouble(), coordinateNode.get(1).asDouble()));
        }
        if (coordinates.size() < 4) {
            throw new IllegalStateException("A land polygon ring has fewer than four coordinates");
        }
        Coordinate first = coordinates.get(0);
        Coordinate last = coordinates.get(coordinates.size() - 1);
        if (!first.equals2D(last)) {
            coordinates.add(new Coordinate(first));
        }
        return GEOMETRY_FACTORY.createLinearRing(coordinates.toArray(Coordinate[]::new));
    }

    /*
     * Normalizes raw track type strings (e.g., "SHIP", "GROUND_VEHICLE")
     *      into a standard PlatformKind enum.
     *
     * @param value Raw track type string.
     * @return Normalized PlatformKind (LAND, SEA, AIR, or UNKNOWN).
     */
    private PlatformKind platformKind(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (normalized) {
            case "LAND", "LAND_VEHICLE", "GROUND_VEHICLE", "VEHICLE" -> PlatformKind.LAND;
            case "SEA", "SHIP", "VESSEL", "MARITIME", "BOAT" -> PlatformKind.SEA;
            case "AIR", "AIRCRAFT", "AERIAL", "PLANE" -> PlatformKind.AIR;
            default -> PlatformKind.UNKNOWN;
        };
    }

    /*
     * Calculates the great-circle distance in kilometers
     *      between two coordinates using the Haversine formula.
     *
     * @param lat1 Latitude of start point.
     * @param lon1 Longitude of start point.
     * @param lat2 Latitude of end point.
     * @param lon2 Longitude of end point.
     * @return Distance in kilometers.
     */
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(Math.min(1, a)));
    }

    /*
     * Computes an intermediate GPS coordinate along the great-circle arc between two endpoints.
     *
     * @param lat1     Start latitude in degrees.
     * @param lon1     Start longitude in degrees.
     * @param lat2     End latitude in degrees.
     * @param lon2     End longitude in degrees.
     * @param fraction Position along the path from 0.0 (start) to 1.0 (end).
     * @return JTS Coordinate containing (longitude, latitude) of the interpolated point.
     */
    private Coordinate interpolateGreatCircle(double lat1, double lon1, double lat2, double lon2, double fraction) {
        if (fraction <= 0) return new Coordinate(lon1, lat1);
        if (fraction >= 1) return new Coordinate(lon2, lat2);

        double phi1 = Math.toRadians(lat1);
        double lambda1 = Math.toRadians(lon1);
        double phi2 = Math.toRadians(lat2);
        double lambda2 = Math.toRadians(lon2);
        double delta = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin((phi2 - phi1) / 2), 2)
                        + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin((lambda2 - lambda1) / 2), 2)));
        if (delta == 0) return new Coordinate(lon1, lat1);

        double a = Math.sin((1 - fraction) * delta) / Math.sin(delta);
        double b = Math.sin(fraction * delta) / Math.sin(delta);
        double x = a * Math.cos(phi1) * Math.cos(lambda1) + b * Math.cos(phi2) * Math.cos(lambda2);
        double y = a * Math.cos(phi1) * Math.sin(lambda1) + b * Math.cos(phi2) * Math.sin(lambda2);
        double z = a * Math.sin(phi1) + b * Math.sin(phi2);
        return new Coordinate(Math.toDegrees(Math.atan2(y, x)), Math.toDegrees(Math.atan2(z, Math.sqrt(x * x + y * y))));
    }

    private enum PlatformKind { LAND, SEA, AIR, UNKNOWN }
}
