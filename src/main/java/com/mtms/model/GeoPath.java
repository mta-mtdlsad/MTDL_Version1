package com.mtms.model;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

// Represents a single path containing 12 points
public class GeoPath {
    private final int pathId;
    private final String pathType;
    private final List<LatLonPoint> points;

    public GeoPath(int pathId, List<LatLonPoint> points, String pathType) {
        this.pathId = pathId;
        this.points = Collections.unmodifiableList(points);
        this.pathType = pathType;
    }

    public int getPathId() { return pathId; }
    public List<LatLonPoint> getPoints() { return points; }

    public String getPathType() {
        return pathType;
    }

    @Override
    public String toString() {
        return "GeoPath{pathId=" + pathId + ", pointCount=" + points.size() + ", points=" + points + "}";
    }
}
