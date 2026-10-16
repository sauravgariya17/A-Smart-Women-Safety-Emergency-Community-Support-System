package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class MapConfigResponse {
    private double centerLat;
    private double centerLng;
    private double boundsRadiusKm;

    public MapConfigResponse(double centerLat, double centerLng, double boundsRadiusKm) {
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.boundsRadiusKm = boundsRadiusKm;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final MapConfigResponse value = new MapConfigResponse();
        public Builder centerLat(double value) { this.value.centerLat = value; return this; }
        public Builder centerLng(double value) { this.value.centerLng = value; return this; }
        public Builder boundsRadiusKm(double value) { this.value.boundsRadiusKm = value; return this; }
        public MapConfigResponse build() { return value; }
    }
}
