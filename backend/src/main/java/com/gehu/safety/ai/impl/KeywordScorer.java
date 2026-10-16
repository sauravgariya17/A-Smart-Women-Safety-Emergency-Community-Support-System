package com.gehu.safety.ai.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** Transparent, explainable weighted keyword matching - not a black box. */
public final class KeywordScorer {

    private KeywordScorer() {}

    public static class ScoreResult {
        public final double score;
        public final List<String> matchedKeywords;

        public ScoreResult(double score, List<String> matchedKeywords) {
            this.score = score;
            this.matchedKeywords = matchedKeywords;
        }
    }

    public static ScoreResult score(String text, Map<String, Double> weightedKeywords) {
        if (text == null || text.isBlank()) {
            return new ScoreResult(0.0, List.of());
        }
        String lower = text.toLowerCase(Locale.ROOT);

        List<String> matched = weightedKeywords.keySet().stream()
                .filter(lower::contains)
                .collect(Collectors.toList());

        double total = matched.stream().mapToDouble(weightedKeywords::get).sum();
        return new ScoreResult(total, matched);
    }
}
