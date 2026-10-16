package com.gehu.safety.ai.dto;

import com.gehu.safety.entity.PriorityLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

@Builder
public class PriorityResult {
    private PriorityLevel priority;
    private String reasoning;

    public PriorityResult(PriorityLevel priority, String reasoning) {
        this.priority = priority;
        this.reasoning = reasoning;
    }
}
