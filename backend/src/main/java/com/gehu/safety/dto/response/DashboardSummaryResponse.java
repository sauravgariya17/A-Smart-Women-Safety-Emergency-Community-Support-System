package com.gehu.safety.dto.response;

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

    public DashboardSummaryResponse(long totalIncidents, long openIncidents, long resolvedIncidents, Map<String, Long> incidentsByCategory, Map<String, Long> incidentsByArea, List<SafetyAnalyticsResponse.DailyCount> incidentTrend, long totalEmergencyEvents, long activeEmergencyEvents, long totalCommunityPosts, long pendingModerationPosts, long moderationPending, long moderationApproved, long moderationRemoved, long moderationAiFlagged, long moderationUserFlagged, int windowDays, String disclaimer) {
        this.totalIncidents = totalIncidents;
        this.openIncidents = openIncidents;
        this.resolvedIncidents = resolvedIncidents;
        this.incidentsByCategory = incidentsByCategory;
        this.incidentsByArea = incidentsByArea;
        this.incidentTrend = incidentTrend;
        this.totalEmergencyEvents = totalEmergencyEvents;
        this.activeEmergencyEvents = activeEmergencyEvents;
        this.totalCommunityPosts = totalCommunityPosts;
        this.pendingModerationPosts = pendingModerationPosts;
        this.moderationPending = moderationPending;
        this.moderationApproved = moderationApproved;
        this.moderationRemoved = moderationRemoved;
        this.moderationAiFlagged = moderationAiFlagged;
        this.moderationUserFlagged = moderationUserFlagged;
        this.windowDays = windowDays;
        this.disclaimer = disclaimer;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final DashboardSummaryResponse value = new DashboardSummaryResponse();
        public Builder totalIncidents(long value) { this.value.totalIncidents = value; return this; }
        public Builder openIncidents(long value) { this.value.openIncidents = value; return this; }
        public Builder resolvedIncidents(long value) { this.value.resolvedIncidents = value; return this; }
        public Builder incidentsByCategory(Map<String, Long> value) { this.value.incidentsByCategory = value; return this; }
        public Builder incidentsByArea(Map<String, Long> value) { this.value.incidentsByArea = value; return this; }
        public Builder incidentTrend(List<SafetyAnalyticsResponse.DailyCount> value) { this.value.incidentTrend = value; return this; }
        public Builder totalEmergencyEvents(long value) { this.value.totalEmergencyEvents = value; return this; }
        public Builder activeEmergencyEvents(long value) { this.value.activeEmergencyEvents = value; return this; }
        public Builder totalCommunityPosts(long value) { this.value.totalCommunityPosts = value; return this; }
        public Builder pendingModerationPosts(long value) { this.value.pendingModerationPosts = value; return this; }
        public Builder moderationPending(long value) { this.value.moderationPending = value; return this; }
        public Builder moderationApproved(long value) { this.value.moderationApproved = value; return this; }
        public Builder moderationRemoved(long value) { this.value.moderationRemoved = value; return this; }
        public Builder moderationAiFlagged(long value) { this.value.moderationAiFlagged = value; return this; }
        public Builder moderationUserFlagged(long value) { this.value.moderationUserFlagged = value; return this; }
        public Builder windowDays(int value) { this.value.windowDays = value; return this; }
        public Builder disclaimer(String value) { this.value.disclaimer = value; return this; }
        public DashboardSummaryResponse build() { return value; }
    }
}
