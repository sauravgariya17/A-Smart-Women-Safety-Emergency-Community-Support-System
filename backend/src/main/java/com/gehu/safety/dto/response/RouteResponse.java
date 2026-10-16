package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor

public class RouteResponse {
    private List<double[]> waypoints;
    private double distanceMeters;
    private int estimatedDurationSeconds;
    private String provider;

    public RouteResponse(List<double[]> waypoints, double distanceMeters, int estimatedDurationSeconds, String provider) {
        this.waypoints = waypoints;
        this.distanceMeters = distanceMeters;
        this.estimatedDurationSeconds = estimatedDurationSeconds;
        this.provider = provider;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final RouteResponse value = new RouteResponse();
        public Builder waypoints(List<double[]> value) { this.value.waypoints = value; return this; }
        public Builder distanceMeters(double value) { this.value.distanceMeters = value; return this; }
        public Builder estimatedDurationSeconds(int value) { this.value.estimatedDurationSeconds = value; return this; }
        public Builder provider(String value) { this.value.provider = value; return this; }
        public RouteResponse build() { return value; }
    }
}
