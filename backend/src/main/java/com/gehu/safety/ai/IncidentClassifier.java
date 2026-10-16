package com.gehu.safety.ai;

import com.gehu.safety.ai.dto.ClassificationResult;

public interface IncidentClassifier {
    ClassificationResult classify(String text);
}
