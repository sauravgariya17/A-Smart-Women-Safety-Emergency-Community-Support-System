package com.gehu.safety.dto.response;

import com.gehu.safety.entity.JourneyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
public class JourneyShareResponse {
    private JourneyStatus status;
    private Double destLat;
    private Double destLng;
    private LocalDateTime expectedArrival;
    private Double lastKnownLat;
    private Double lastKnownLng;
    private LocalDateTime lastPingAt;
}
