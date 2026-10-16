package com.gehu.safety.ai;

import com.gehu.safety.ai.dto.ModerationResult;

public interface ContentModerationAnalyzer {
    ModerationResult analyze(String text, Long authorUserId);
}
