package com.gehu.safety.dto.response;

import com.gehu.safety.entity.JourneyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
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
}
