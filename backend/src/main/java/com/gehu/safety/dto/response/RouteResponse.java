package com.gehu.safety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private List<double[]> waypoints;
    private double distanceMeters;
    private int estimatedDurationSeconds;
    private String provider;
}
