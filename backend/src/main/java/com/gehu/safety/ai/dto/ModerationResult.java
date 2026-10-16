package com.gehu.safety.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationResult {
    private boolean spam;
    private boolean toxic;
    private boolean inappropriate;
    private boolean suspiciousRepeat;
    private String reasoning;

    public boolean shouldQueue() {
        return spam || toxic || inappropriate || suspiciousRepeat;
    }
}
