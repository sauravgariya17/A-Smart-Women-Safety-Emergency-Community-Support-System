package com.gehu.safety.dto.response;

import com.gehu.safety.entity.JourneyStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This is the OWNER's view - safe to include the tracking token here since
 * only the authenticated journey owner ever receives this DTO. The link
 * built from `trackingToken` is what gets shared with trusted contacts;
 * see JourneyShareResponse for what a contact (no login) is allowed to see.
 */
@Data
@NoArgsConstructor

public class JourneyResponse {
    private Long id;
    private Double startLat;
    private Double startLng;
    private Double destLat;
    private Double destLng;
    private LocalDateTime startTime;
    private LocalDateTime expectedArrival;
    private JourneyStatus status;
    private Double lastKnownLat;
    private Double lastKnownLng;
    private LocalDateTime lastPingAt;
    private String trackingToken;
    private String shareUrl; // frontend-ready link, e.g. https://app/journey/track/{token}
    private RouteResponse route;
    private List<String> notifiedContactNames;

    public JourneyResponse(Long id, Double startLat, Double startLng, Double destLat, Double destLng, LocalDateTime startTime, LocalDateTime expectedArrival, JourneyStatus status, Double lastKnownLat, Double lastKnownLng, LocalDateTime lastPingAt, String trackingToken, RouteResponse route, List<String> notifiedContactNames) {
        this.id = id;
        this.startLat = startLat;
        this.startLng = startLng;
        this.destLat = destLat;
        this.destLng = destLng;
        this.startTime = startTime;
        this.expectedArrival = expectedArrival;
        this.status = status;
        this.lastKnownLat = lastKnownLat;
        this.lastKnownLng = lastKnownLng;
        this.lastPingAt = lastPingAt;
        this.trackingToken = trackingToken;
        this.route = route;
        this.notifiedContactNames = notifiedContactNames;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final JourneyResponse value = new JourneyResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder startLat(Double value) { this.value.startLat = value; return this; }
        public Builder startLng(Double value) { this.value.startLng = value; return this; }
        public Builder destLat(Double value) { this.value.destLat = value; return this; }
        public Builder destLng(Double value) { this.value.destLng = value; return this; }
        public Builder startTime(LocalDateTime value) { this.value.startTime = value; return this; }
        public Builder expectedArrival(LocalDateTime value) { this.value.expectedArrival = value; return this; }
        public Builder status(JourneyStatus value) { this.value.status = value; return this; }
        public Builder lastKnownLat(Double value) { this.value.lastKnownLat = value; return this; }
        public Builder lastKnownLng(Double value) { this.value.lastKnownLng = value; return this; }
        public Builder lastPingAt(LocalDateTime value) { this.value.lastPingAt = value; return this; }
        public Builder trackingToken(String value) { this.value.trackingToken = value; return this; }
        public Builder shareUrl(String value) { this.value.shareUrl = value; return this; }
        public Builder route(RouteResponse value) { this.value.route = value; return this; }
        public Builder notifiedContactNames(List<String> value) { this.value.notifiedContactNames = value; return this; }
        public JourneyResponse build() { return value; }
    }
}
