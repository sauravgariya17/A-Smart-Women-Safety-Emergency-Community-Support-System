package com.gehu.safety.ai.impl;

import com.gehu.safety.ai.PriorityAssessor;
import com.gehu.safety.ai.dto.PriorityResult;
import com.gehu.safety.entity.IncidentCategory;
import com.gehu.safety.entity.PriorityLevel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Priority = category baseline severity, escalated by urgency keywords.
 * Auditable by design: a moderator can see exactly why a level was
 * suggested. Never final - always editable by an admin.
 */
@Component
public class RuleBasedPriorityAssessor implements PriorityAssessor {

    private static final Map<IncidentCategory, PriorityLevel> BASELINE = Map.of(
            IncidentCategory.EMERGENCY, PriorityLevel.CRITICAL,
            IncidentCategory.PHYSICAL_THREAT, PriorityLevel.HIGH,
            IncidentCategory.STALKING, PriorityLevel.HIGH,
            IncidentCategory.HARASSMENT, PriorityLevel.MEDIUM,
            IncidentCategory.VERBAL_ABUSE, PriorityLevel.MEDIUM,
            IncidentCategory.UNSAFE_AREA, PriorityLevel.LOW,
            IncidentCategory.OTHER, PriorityLevel.LOW
    );

    private static final Map<String, Integer> ESCALATION_KEYWORDS = Map.of(
            "weapon", 2, "knife", 2, "can't breathe", 2, "bleeding", 2,
            "right now", 1, "still here", 1, "won't stop", 1, "every day", 1
    );

    @Override
    public PriorityResult assess(String text, IncidentCategory category) {
        PriorityLevel baseline = BASELINE.getOrDefault(category, PriorityLevel.LOW);

        KeywordScorer.ScoreResult escalation = KeywordScorer.score(
                text, ESCALATION_KEYWORDS.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (double) e.getValue())));

        int escalationSteps = (int) Math.min(2, Math.round(escalation.score));
        PriorityLevel finalPriority = escalate(baseline, escalationSteps);

        String reasoning = escalationSteps > 0
                ? String.format("Base priority for %s is %s, escalated due to: %s",
                        category, baseline, String.join(", ", escalation.matchedKeywords))
                : String.format("Base priority for category %s", category);

        return PriorityResult.builder()
                .priority(finalPriority)
                .reasoning(reasoning)
                .build();
    }

    private PriorityLevel escalate(PriorityLevel level, int steps) {
        PriorityLevel[] order = PriorityLevel.values();
        int idx = Math.min(order.length - 1, level.ordinal() + steps);
        return order[idx];
    }
}
