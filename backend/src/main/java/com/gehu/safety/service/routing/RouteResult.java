package com.gehu.safety.service.routing;

import java.util.List;

/**
 * A route is a list of [lat, lng] waypoints the frontend draws as a polyline,
 * plus a rough distance/duration estimate. StraightLineRoutingProvider
 * returns a 2-point route (start, end); a future OSRM/ORS-backed provider
 * would return the same shape with many more waypoints - no DTO change needed.
 */
public class RouteResult {
    private List<double[]> waypoints; // each element is [lat, lng]
    private double distanceMeters;
    private int estimatedDurationSeconds;
    private String provider; // e.g. "straight-line", "osrm" - shown for transparency, not hidden

    public RouteResult(double distanceMeters, int estimatedDurationSeconds) {
        this.distanceMeters = distanceMeters;
        this.estimatedDurationSeconds = estimatedDurationSeconds;
    }

    public RouteResult() {}

    public List<double[]> getWaypoints() { return waypoints; }
    public void setWaypoints(List<double[]> waypoints) { this.waypoints = waypoints; }
    public double getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }
    public int getEstimatedDurationSeconds() { return estimatedDurationSeconds; }
    public void setEstimatedDurationSeconds(int estimatedDurationSeconds) { this.estimatedDurationSeconds = estimatedDurationSeconds; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final RouteResult value = new RouteResult();
        public Builder waypoints(List<double[]> value) { this.value.waypoints = value; return this; }
        public Builder distanceMeters(double value) { this.value.distanceMeters = value; return this; }
        public Builder estimatedDurationSeconds(int value) { this.value.estimatedDurationSeconds = value; return this; }
        public Builder provider(String value) { this.value.provider = value; return this; }
        public RouteResult build() { return value; }
    }
}
