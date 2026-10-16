package com.gehu.safety.dto.response;

import com.gehu.safety.entity.IncidentCategory;
import com.gehu.safety.entity.PriorityLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response to the preview-only /analyze endpoint - nothing persisted yet. */
@Data
@NoArgsConstructor

public class IncidentAnalysisResponse {
    private IncidentCategory suggestedCategory;
    private double confidence;
    private String categoryReasoning;
    private PriorityLevel suggestedPriority;
    private String priorityReasoning;
    private String summary; // null if description was short enough not to need one

    public IncidentAnalysisResponse(IncidentCategory suggestedCategory, double confidence, String categoryReasoning, PriorityLevel suggestedPriority, String priorityReasoning) {
        this.suggestedCategory = suggestedCategory;
        this.confidence = confidence;
        this.categoryReasoning = categoryReasoning;
        this.suggestedPriority = suggestedPriority;
        this.priorityReasoning = priorityReasoning;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final IncidentAnalysisResponse value = new IncidentAnalysisResponse();
        public Builder suggestedCategory(IncidentCategory value) { this.value.suggestedCategory = value; return this; }
        public Builder confidence(double value) { this.value.confidence = value; return this; }
        public Builder categoryReasoning(String value) { this.value.categoryReasoning = value; return this; }
        public Builder suggestedPriority(PriorityLevel value) { this.value.suggestedPriority = value; return this; }
        public Builder priorityReasoning(String value) { this.value.priorityReasoning = value; return this; }
        public Builder summary(String value) { this.value.summary = value; return this; }
        public IncidentAnalysisResponse build() { return value; }
    }
}
