package com.gehu.safety.dto.response;

import com.gehu.safety.entity.IncidentCategory;
import com.gehu.safety.entity.PriorityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response to the preview-only /analyze endpoint - nothing persisted yet. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentAnalysisResponse {
    private IncidentCategory suggestedCategory;
    private double confidence;
    private String categoryReasoning;
    private PriorityLevel suggestedPriority;
    private String priorityReasoning;
    private String summary; // null if description was short enough not to need one
}
