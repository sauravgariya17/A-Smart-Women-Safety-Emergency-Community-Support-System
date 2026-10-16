package com.gehu.safety.dto.response;

import com.gehu.safety.entity.JourneyStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PRIVACY NOTE: This is deliberately the ONLY response shape reachable via
 * the public tracking-link endpoint (/api/journeys/track/{token}). It has
 * no user name, email, phone, or handle - a trusted contact opening the
 * link sees "where they are on this journey right now," nothing about the
 * account itself. The token is the only credential; it expires when the
 * journey ends (server rejects lookups for non-ACTIVE journeys).
 */
@Data
@NoArgsConstructor

public class JourneyShareResponse {
    private JourneyStatus status;
    private Double destLat;
    private Double destLng;
    private LocalDateTime expectedArrival;
    private Double lastKnownLat;
    private Double lastKnownLng;
    private LocalDateTime lastPingAt;

    public JourneyShareResponse(JourneyStatus status, Double destLat, Double destLng, LocalDateTime expectedArrival, Double lastKnownLat, Double lastKnownLng, LocalDateTime lastPingAt) {
        this.status = status;
        this.destLat = destLat;
        this.destLng = destLng;
        this.expectedArrival = expectedArrival;
        this.lastKnownLat = lastKnownLat;
        this.lastKnownLng = lastKnownLng;
        this.lastPingAt = lastPingAt;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final JourneyShareResponse value = new JourneyShareResponse();
        public Builder status(JourneyStatus value) { this.value.status = value; return this; }
        public Builder destLat(Double value) { this.value.destLat = value; return this; }
        public Builder destLng(Double value) { this.value.destLng = value; return this; }
        public Builder expectedArrival(LocalDateTime value) { this.value.expectedArrival = value; return this; }
        public Builder lastKnownLat(Double value) { this.value.lastKnownLat = value; return this; }
        public Builder lastKnownLng(Double value) { this.value.lastKnownLng = value; return this; }
        public Builder lastPingAt(LocalDateTime value) { this.value.lastPingAt = value; return this; }
        public JourneyShareResponse build() { return value; }
    }
}
