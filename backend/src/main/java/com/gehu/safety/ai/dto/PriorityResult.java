package com.gehu.safety.ai.dto;

import com.gehu.safety.entity.PriorityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriorityResult {
    private PriorityLevel priority;
    private String reasoning;
}
