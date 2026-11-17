package com.mtms.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "paths")
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pathId;

    private String pathName;
    private double distanceKm;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;

    public Path() {}

    public Path(Long pathId, String pathName, double distanceKm,
                double startLatitude, double startLongitude,
                double endLatitude, double endLongitude) {
        this.pathId = pathId;
        this.pathName = pathName;
        this.distanceKm = distanceKm;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getPathName() { return pathName; }
    public void setPathName(String pathName) { this.pathName = pathName; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public double getStartLatitude() { return startLatitude; }
    public void setStartLatitude(double startLatitude) { this.startLatitude = startLatitude; }

    public double getStartLongitude() { return startLongitude; }
    public void setStartLongitude(double startLongitude) { this.startLongitude = startLongitude; }

    public double getEndLatitude() { return endLatitude; }
    public void setEndLatitude(double endLatitude) { this.endLatitude = endLatitude; }

    public double getEndLongitude() { return endLongitude; }
    public void setEndLongitude(double endLongitude) { this.endLongitude = endLongitude; }

    public double getProgressForCoordinates(double lat, double lon) {
        double abLat = endLatitude - startLatitude;
        double abLon = endLongitude - startLongitude;

        double apLat = lat - startLatitude;
        double apLon = lon - startLongitude;

        double abLenSq = (abLat * abLat) + (abLon * abLon);
        if (abLenSq == 0.0) return 0.0;

        double t = ((apLat * abLat) + (apLon * abLon)) / abLenSq;
        return Math.max(0.0, Math.min(1.0, t));
    }

    public double getDistanceFromPathKm(double lat, double lon) {
        double t = getProgressForCoordinates(lat, lon);

        double closestLat = startLatitude + t * (endLatitude - startLatitude);
        double closestLon = startLongitude + t * (endLongitude - startLongitude);

        double latDiff = (lat - closestLat) * 111.0;
        double lonDiff = (lon - closestLon) * 111.0 * Math.cos(Math.toRadians(startLatitude));

        return Math.sqrt((latDiff * latDiff) + (lonDiff * lonDiff));
    }

    public boolean isTrackOnPath(double lat, double lon, double maxDeviationKm) {
        return getDistanceFromPathKm(lat, lon) <= maxDeviationKm;
    }

    @Override
    public String toString() {
        return "Path{" +
                "pathId=" + pathId +
                ", pathName='" + pathName + '\'' +
                ", distanceKm=" + distanceKm +
                '}';
    }
}
