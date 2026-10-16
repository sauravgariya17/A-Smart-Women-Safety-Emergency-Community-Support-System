package com.gehu.safety.dto.response;

import com.gehu.safety.entity.SafeZoneType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class SafeZoneResponse {
    private Long id;
    private String name;
    private SafeZoneType type;
    private Double lat;
    private Double lng;
    private String description;

    public SafeZoneResponse(Long id, String name, SafeZoneType type, Double lat, Double lng, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final SafeZoneResponse value = new SafeZoneResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder name(String value) { this.value.name = value; return this; }
        public Builder type(SafeZoneType value) { this.value.type = value; return this; }
        public Builder lat(Double value) { this.value.lat = value; return this; }
        public Builder lng(Double value) { this.value.lng = value; return this; }
        public Builder description(String value) { this.value.description = value; return this; }
        public SafeZoneResponse build() { return value; }
    }
}
