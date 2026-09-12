package com.gehu.safety.service.routing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A route is a list of [lat, lng] waypoints the frontend draws as a polyline,
 * plus a rough distance/duration estimate. StraightLineRoutingProvider
 * returns a 2-point route (start, end); a future OSRM/ORS-backed provider
 * would return the same shape with many more waypoints - no DTO change needed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResult {
    private List<double[]> waypoints; // each element is [lat, lng]
    private double distanceMeters;
    private int estimatedDurationSeconds;
    private String provider; // e.g. "straight-line", "osrm" - shown for transparency, not hidden
}
