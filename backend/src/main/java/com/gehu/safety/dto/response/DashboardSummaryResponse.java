package com.gehu.safety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * The single "home page" aggregation for the admin/moderator dashboard.
 * Every number here is a COUNT - no individual incident, SOS, post, or
 * user record is embedded in this response. Area-level breakdowns reuse
 * the same k-anonymity suppression as SafetyAnalyticsResponse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {
    // Incidents
    private long totalIncidents;
    private long openIncidents;
    private long resolvedIncidents;
    private Map<String, Long> incidentsByCategory;
    private Map<String, Long> incidentsByArea;
    private List<SafetyAnalyticsResponse.DailyCount> incidentTrend;

    // Emergencies
    private long totalEmergencyEvents;
    private long activeEmergencyEvents;

    // Community
    private long totalCommunityPosts;
    private long pendingModerationPosts;

    // Moderation
    private long moderationPending;
    private long moderationApproved;
    private long moderationRemoved;
    private long moderationAiFlagged;
    private long moderationUserFlagged;

    private int windowDays;
    private String disclaimer;
}
