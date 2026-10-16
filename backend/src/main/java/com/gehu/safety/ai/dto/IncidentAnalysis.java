package com.gehu.safety.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentAnalysis {
    private ClassificationResult classification;
    private PriorityResult priority;
    private String summary;
}
