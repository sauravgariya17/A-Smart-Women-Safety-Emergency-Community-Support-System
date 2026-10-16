package com.gehu.safety.ai;

import com.gehu.safety.ai.dto.ClassificationResult;

/**
 * OPTIONAL UPGRADE PATH - not required for this project to function.
 * Implement against any hosted LLM API (called via plain HTTP/WebClient
 * directly from this Java backend - no Python needed even for this) and
 * try it before RuleBasedIncidentClassifier, catching failures to fall
 * back to the rule-based result. No implementation is wired in by default
 * (ai.llm.enabled=false) so the project runs with zero external
 * dependencies or API costs out of the box.
 */
public interface LlmClassifierClient {
    ClassificationResult classify(String text) throws Exception;
}
