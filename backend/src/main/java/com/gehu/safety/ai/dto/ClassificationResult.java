package com.gehu.safety.ai.dto;

import com.gehu.safety.entity.IncidentCategory;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Builder
public class ClassificationResult {
    private IncidentCategory category;
    private double confidence;
    private String reasoning;
}
