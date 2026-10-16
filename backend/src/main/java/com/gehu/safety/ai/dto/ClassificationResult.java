package com.gehu.safety.ai.dto;

import com.gehu.safety.entity.IncidentCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ASSISTING RECOMMENDATION, NOT A DECISION: confidence + reasoning let a
 * human (reporter or admin) judge whether to trust this suggestion rather
 * than accept it blindly. Always labelled "AI suggested" wherever shown.
 */
@Data
@NoArgsConstructor

@Builder
public class ClassificationResult {
    private IncidentCategory category;
    private double confidence;
    private String reasoning;

    public ClassificationResult(IncidentCategory category, double confidence, String reasoning) {
        this.category = category;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }
}
