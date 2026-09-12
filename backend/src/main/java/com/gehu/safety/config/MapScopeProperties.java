package com.gehu.safety.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.map")
@Data
public class MapScopeProperties {
    private double centerLat;
    private double centerLng;
    private double boundsRadiusKm;
    private int journeyCheckinGraceMinutes;
}
