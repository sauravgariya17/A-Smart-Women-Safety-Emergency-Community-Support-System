package com.gehu.safety.ai.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

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

    public ModerationResult(boolean spam, boolean toxic, boolean inappropriate, boolean suspiciousRepeat, String reasoning) {
        this.spam = spam;
        this.toxic = toxic;
        this.inappropriate = inappropriate;
        this.suspiciousRepeat = suspiciousRepeat;
        this.reasoning = reasoning;
    }
}
