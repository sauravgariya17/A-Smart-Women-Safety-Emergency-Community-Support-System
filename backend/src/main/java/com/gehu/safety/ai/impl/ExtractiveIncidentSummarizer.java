package com.gehu.safety.ai.impl;

import com.gehu.safety.ai.IncidentSummarizer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extractive, not abstractive: only ever shows the reporter's own words,
 * just fewer of them - safer than an LLM inventing phrasing for a safety report.
 */
@Component
public class ExtractiveIncidentSummarizer implements IncidentSummarizer {

    private static final int MIN_LENGTH_TO_SUMMARIZE = 400;
    private static final int MAX_SENTENCES = 3;

    @Override
    public String summarize(String text) {
        if (text == null || text.length() < MIN_LENGTH_TO_SUMMARIZE) {
            return null;
        }

        List<String> sentences = Arrays.stream(text.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (sentences.size() <= MAX_SENTENCES) {
            return text;
        }

        String first = sentences.get(0);
        List<String> rest = sentences.subList(1, sentences.size()).stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .limit(MAX_SENTENCES - 1)
                .collect(Collectors.toList());

        StringBuilder summary = new StringBuilder(first);
        for (String s : rest) {
            summary.append(" ").append(s);
        }
        return summary.toString();
    }
}
