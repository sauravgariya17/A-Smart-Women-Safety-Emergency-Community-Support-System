package com.gehu.safety.ai;

import com.gehu.safety.ai.dto.IncidentAnalysis;
import com.gehu.safety.ai.dto.ModerationResult;
import org.springframework.stereotype.Service;

/**
 * FACADE: the rest of the application depends on this class, not the
 * individual classifier/priority/moderation/summarizer classes directly.
 * IncidentReportService and Community post/comment services call this.
 */
@Service
public class AiTextAnalysisService {

    private final IncidentClassifier classifier;
    private final PriorityAssessor priorityAssessor;
    private final ContentModerationAnalyzer moderationAnalyzer;
    private final IncidentSummarizer summarizer;


    public AiTextAnalysisService(IncidentClassifier classifier,
            PriorityAssessor priorityAssessor,
            ContentModerationAnalyzer moderationAnalyzer,
            IncidentSummarizer summarizer) {
        this.classifier = classifier;
        this.priorityAssessor = priorityAssessor;
        this.moderationAnalyzer = moderationAnalyzer;
        this.summarizer = summarizer;
    }
    public IncidentAnalysis analyzeIncident(String description) {
        var classification = classifier.classify(description);
        var priority = priorityAssessor.assess(description, classification.getCategory());
        var summary = summarizer.summarize(description);

        return IncidentAnalysis.builder()
                .classification(classification)
                .priority(priority)
                .summary(summary)
                .build();
    }

    public ModerationResult analyzeForModeration(String text, Long authorUserId) {
        return moderationAnalyzer.analyze(text, authorUserId);
    }
}
