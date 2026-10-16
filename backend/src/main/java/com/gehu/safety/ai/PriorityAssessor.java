package com.gehu.safety.ai;

import com.gehu.safety.ai.dto.PriorityResult;
import com.gehu.safety.entity.IncidentCategory;

public interface PriorityAssessor {
    PriorityResult assess(String text, IncidentCategory category);
}
