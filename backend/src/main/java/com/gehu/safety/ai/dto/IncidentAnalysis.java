package com.gehu.safety.ai.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

@Builder
public class IncidentAnalysis {
    private ClassificationResult classification;
    private PriorityResult priority;
    private String summary;

    public IncidentAnalysis(ClassificationResult classification, PriorityResult priority, String summary) {
        this.classification = classification;
        this.priority = priority;
        this.summary = summary;
    }
}
