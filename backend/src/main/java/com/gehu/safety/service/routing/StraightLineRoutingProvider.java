package com.gehu.safety.service.routing;

import com.gehu.safety.util.GeoUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * V1 fallback: draws a straight line between start and destination and
 * estimates duration at an average walking speed. This is intentionally
 * simple - see RoutingProvider's javadoc for the upgrade path to OSRM/ORS.
 * Good enough to show "roughly this direction, roughly this long" on a
 * small campus, which is the actual requirement for Journey Mode v1.
 */
@Component
public class StraightLineRoutingProvider implements RoutingProvider {

    private static final double AVERAGE_WALKING_SPEED_KMH = 4.5;

    @Override
    public RouteResult getRoute(double startLat, double startLng, double destLat, double destLng) {
        double distanceKm = GeoUtils.distanceKm(startLat, startLng, destLat, destLng);
        int durationSeconds = (int) Math.round((distanceKm / AVERAGE_WALKING_SPEED_KMH) * 3600);

        return RouteResult.builder()
                .waypoints(List.of(
                        new double[]{startLat, startLng},
                        new double[]{destLat, destLng}
                ))
                .distanceMeters(distanceKm * 1000)
                .estimatedDurationSeconds(durationSeconds)
                .provider("straight-line-v1")
                .build();
    }
}
