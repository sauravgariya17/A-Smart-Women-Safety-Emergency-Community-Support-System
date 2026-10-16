package com.gehu.safety.dto.response;

import com.gehu.safety.entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Admin/moderator view. Identity is shown ONLY when visibilityType allows it
 * (NAMED_ADMIN_ONLY) - for ANON_PUBLIC/FULL_ANON reports, reporterName is
 * null even to admins, same anonymity-boundary discipline as Community.
 * aiSummary is included here specifically because it's an admin-only feature.
 */
@Data
@NoArgsConstructor

public class IncidentAdminResponse {
    private Long id;
    private String reporterName; // null unless visibilityType == NAMED_ADMIN_ONLY
    private String description;
    private String aiSummary;
    private IncidentCategory category;
    private PriorityLevel priority;
    private IncidentCategory aiSuggestedCategory;
    private PriorityLevel aiSuggestedPriority;
    private Double aiConfidence;
    private String aiReasoning;
    private String assignedToName;
    private Double lat;
    private Double lng;
    private String areaLabel;
    private IncidentVisibility visibilityType;
    private IncidentStatus status;
    private LocalDateTime createdAt;

    public IncidentAdminResponse(Long id, String description, String aiSummary, IncidentCategory category, PriorityLevel priority, IncidentCategory aiSuggestedCategory, PriorityLevel aiSuggestedPriority, Double aiConfidence, String aiReasoning, String assignedToName, Double lat, Double lng, String areaLabel, IncidentVisibility visibilityType, IncidentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.aiSummary = aiSummary;
        this.category = category;
        this.priority = priority;
        this.aiSuggestedCategory = aiSuggestedCategory;
        this.aiSuggestedPriority = aiSuggestedPriority;
        this.aiConfidence = aiConfidence;
        this.aiReasoning = aiReasoning;
        this.assignedToName = assignedToName;
        this.lat = lat;
        this.lng = lng;
        this.areaLabel = areaLabel;
        this.visibilityType = visibilityType;
        this.status = status;
        this.createdAt = createdAt;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final IncidentAdminResponse value = new IncidentAdminResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder reporterName(String value) { this.value.reporterName = value; return this; }
        public Builder description(String value) { this.value.description = value; return this; }
        public Builder aiSummary(String value) { this.value.aiSummary = value; return this; }
        public Builder category(IncidentCategory value) { this.value.category = value; return this; }
        public Builder priority(PriorityLevel value) { this.value.priority = value; return this; }
        public Builder aiSuggestedCategory(IncidentCategory value) { this.value.aiSuggestedCategory = value; return this; }
        public Builder aiSuggestedPriority(PriorityLevel value) { this.value.aiSuggestedPriority = value; return this; }
        public Builder aiConfidence(Double value) { this.value.aiConfidence = value; return this; }
        public Builder aiReasoning(String value) { this.value.aiReasoning = value; return this; }
        public Builder assignedToName(String value) { this.value.assignedToName = value; return this; }
        public Builder lat(Double value) { this.value.lat = value; return this; }
        public Builder lng(Double value) { this.value.lng = value; return this; }
        public Builder areaLabel(String value) { this.value.areaLabel = value; return this; }
        public Builder visibilityType(IncidentVisibility value) { this.value.visibilityType = value; return this; }
        public Builder status(IncidentStatus value) { this.value.status = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public IncidentAdminResponse build() { return value; }
    }
}
