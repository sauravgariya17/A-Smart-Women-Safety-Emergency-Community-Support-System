package com.gehu.safety.ai.impl;

import com.gehu.safety.ai.IncidentClassifier;
import com.gehu.safety.ai.dto.ClassificationResult;
import com.gehu.safety.entity.IncidentCategory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Scores text against every category's keyword set, picks the highest.
 * Falls back to OTHER with low confidence when nothing matches strongly -
 * an honest "I don't know" beats a confident wrong answer.
 */
@Component
public class RuleBasedIncidentClassifier implements IncidentClassifier {

    private static final Map<IncidentCategory, Map<String, Double>> CATEGORY_KEYWORDS = new LinkedHashMap<>();

    static {
        CATEGORY_KEYWORDS.put(IncidentCategory.PHYSICAL_THREAT, Map.of(
                "hit", 2.0, "attack", 2.5, "grabbed", 2.5, "punch", 2.5, "weapon", 3.0,
                "knife", 3.0, "hurt me", 2.5, "pushed", 2.0, "assault", 3.0
        ));
        CATEGORY_KEYWORDS.put(IncidentCategory.EMERGENCY, Map.of(
                "help me", 2.5, "emergency", 3.0, "urgent", 2.0, "in danger", 3.0,
                "right now", 1.5, "trapped", 2.5
        ));
        CATEGORY_KEYWORDS.put(IncidentCategory.STALKING, Map.of(
                "following me", 3.0, "followed", 2.5, "watching me", 2.5, "shows up wherever",
                2.0, "keeps appearing", 2.0, "stalking", 3.0, "outside my hostel", 2.0
        ));
        CATEGORY_KEYWORDS.put(IncidentCategory.HARASSMENT, Map.of(
                "harass", 2.5, "won't leave me alone", 2.0, "unwanted", 1.5,
                "inappropriate comments", 2.0, "touched", 2.0, "uncomfortable", 1.0
        ));
        CATEGORY_KEYWORDS.put(IncidentCategory.VERBAL_ABUSE, Map.of(
                "shouted at", 1.5, "verbally abused", 2.5, "insulted", 2.0,
                "yelled", 1.5, "threatening words", 2.0, "abusive language", 2.5
        ));
        CATEGORY_KEYWORDS.put(IncidentCategory.UNSAFE_AREA, Map.of(
                "dark", 1.0, "no lighting", 1.5, "poorly lit", 1.5, "isolated", 1.5,
                "unsafe area", 2.5, "no cctv", 1.5, "deserted", 1.5
        ));
    }

    private static final double CONFIDENCE_SCALE = 4.0;

    @Override
    public ClassificationResult classify(String text) {
        IncidentCategory bestCategory = IncidentCategory.OTHER;
        double bestScore = 0.0;
        String bestReasoning = "No strong category signals found in the description";

        for (var entry : CATEGORY_KEYWORDS.entrySet()) {
            KeywordScorer.ScoreResult result = KeywordScorer.score(text, entry.getValue());
            if (result.score > bestScore) {
                bestScore = result.score;
                bestCategory = entry.getKey();
                bestReasoning = result.matchedKeywords.isEmpty()
                        ? bestReasoning
                        : "Matched keywords: " + String.join(", ", result.matchedKeywords);
            }
        }

        double confidence = bestScore == 0.0 ? 0.2 : Math.min(0.95, bestScore / CONFIDENCE_SCALE);

        return ClassificationResult.builder()
                .category(bestCategory)
                .confidence(confidence)
                .reasoning(bestReasoning)
                .build();
    }
}
