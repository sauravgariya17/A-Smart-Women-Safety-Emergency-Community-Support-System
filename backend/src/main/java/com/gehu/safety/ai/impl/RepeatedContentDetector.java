package com.gehu.safety.ai.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Jaccard similarity over word sets - simple, fast, explainable in a viva. */
public final class RepeatedContentDetector {

    private static final double SIMILARITY_THRESHOLD = 0.75;

    private RepeatedContentDetector() {}

    public static boolean isNearDuplicate(String newText, Iterable<String> recentTextsBySameAuthor) {
        Set<String> newWords = wordSet(newText);
        if (newWords.isEmpty()) return false;

        for (String previous : recentTextsBySameAuthor) {
            double similarity = jaccardSimilarity(newWords, wordSet(previous));
            if (similarity >= SIMILARITY_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> wordSet(String text) {
        if (text == null) return Set.of();
        return new HashSet<>(Arrays.asList(text.toLowerCase().trim().split("\\s+")));
    }

    private static double jaccardSimilarity(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 1.0;
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}
