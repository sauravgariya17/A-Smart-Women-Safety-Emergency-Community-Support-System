package com.gehu.safety.dto.response;

import com.gehu.safety.entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** The reporter's own view of their submitted report. */
@Data
@NoArgsConstructor

public class IncidentResponse {
    private Long id;
    private String description;
    private IncidentCategory category;
    private PriorityLevel priority;
    private IncidentCategory aiSuggestedCategory;
    private PriorityLevel aiSuggestedPriority;
    private Double aiConfidence;
    private String areaLabel;
    private IncidentVisibility visibilityType;
    private IncidentStatus status;
    private LocalDateTime createdAt;

    public IncidentResponse(Long id, String description, IncidentCategory category, PriorityLevel priority, IncidentCategory aiSuggestedCategory, PriorityLevel aiSuggestedPriority, Double aiConfidence, String areaLabel, IncidentVisibility visibilityType, IncidentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.aiSuggestedCategory = aiSuggestedCategory;
        this.aiSuggestedPriority = aiSuggestedPriority;
        this.aiConfidence = aiConfidence;
        this.areaLabel = areaLabel;
        this.visibilityType = visibilityType;
        this.status = status;
        this.createdAt = createdAt;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final IncidentResponse value = new IncidentResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder description(String value) { this.value.description = value; return this; }
        public Builder category(IncidentCategory value) { this.value.category = value; return this; }
        public Builder priority(PriorityLevel value) { this.value.priority = value; return this; }
        public Builder aiSuggestedCategory(IncidentCategory value) { this.value.aiSuggestedCategory = value; return this; }
        public Builder aiSuggestedPriority(PriorityLevel value) { this.value.aiSuggestedPriority = value; return this; }
        public Builder aiConfidence(Double value) { this.value.aiConfidence = value; return this; }
        public Builder areaLabel(String value) { this.value.areaLabel = value; return this; }
        public Builder visibilityType(IncidentVisibility value) { this.value.visibilityType = value; return this; }
        public Builder status(IncidentStatus value) { this.value.status = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public IncidentResponse build() { return value; }
    }
}
