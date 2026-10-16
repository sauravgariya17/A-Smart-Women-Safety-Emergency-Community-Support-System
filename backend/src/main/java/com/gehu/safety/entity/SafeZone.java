package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "safe_zones")
@Getter
@Setter
public class SafeZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 60)
    private SafeZoneType type;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "description", length = 300)
    private String description;

    public SafeZone(Long id, String name, SafeZoneType type, Double lat, Double lng, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
    }

    public SafeZone() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final SafeZone value = new SafeZone();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder name(String value) { this.value.name = value; return this; }
        public Builder type(SafeZoneType value) { this.value.type = value; return this; }
        public Builder lat(Double value) { this.value.lat = value; return this; }
        public Builder lng(Double value) { this.value.lng = value; return this; }
        public Builder description(String value) { this.value.description = value; return this; }
        public SafeZone build() { return value; }
    }
}
