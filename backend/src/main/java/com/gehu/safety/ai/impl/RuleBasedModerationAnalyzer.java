package com.gehu.safety.ai.impl;

import com.gehu.safety.ai.ContentModerationAnalyzer;
import com.gehu.safety.ai.dto.ModerationResult;
import com.gehu.safety.repository.CommentRepository;
import com.gehu.safety.repository.CommunityPostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class RuleBasedModerationAnalyzer implements ContentModerationAnalyzer {

    private static final List<String> SEVERE_KEYWORDS = List.of(
            "kill", "rape", "suicide", "murder", "attack her", "stalk her home"
    );
    private static final List<String> TOXIC_KEYWORDS = List.of(
            "idiot", "stupid bitch", "whore", "slut", "shut up you"
    );
    private static final List<String> SPAM_SIGNALS = List.of(
            "http://", "https://", "www.", "buy now", "click here", "subscribe to", "limited offer", "earn money"
    );
    private static final List<String> INAPPROPRIATE_KEYWORDS = List.of(
            "nude", "sex tape", "explicit photo"
    );

    private static final int LOOKBACK_DAYS = 3;
    private static final int RECENT_ITEMS_TO_CHECK = 10;

    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;


    public RuleBasedModerationAnalyzer(CommunityPostRepository postRepository,
            CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }
    @Override
    public ModerationResult analyze(String text, Long authorUserId) {
        String lower = text == null ? "" : text.toLowerCase(Locale.ROOT);

        boolean severe = SEVERE_KEYWORDS.stream().anyMatch(lower::contains);
        boolean toxic = severe || TOXIC_KEYWORDS.stream().anyMatch(lower::contains);
        boolean spam = SPAM_SIGNALS.stream().anyMatch(lower::contains);
        boolean inappropriate = INAPPROPRIATE_KEYWORDS.stream().anyMatch(lower::contains);
        boolean repeated = isRepeated(text, authorUserId);

        StringBuilder reasoning = new StringBuilder();
        if (severe) reasoning.append("Contains potentially severe/violent language. ");
        if (toxic && !severe) reasoning.append("Contains toxic/abusive language. ");
        if (spam) reasoning.append("Contains spam-like signals (links/promotional phrasing). ");
        if (inappropriate) reasoning.append("Contains sexually explicit references. ");
        if (repeated) reasoning.append("Near-duplicate of this author's recent content. ");
        if (reasoning.length() == 0) reasoning.append("No moderation signals detected.");

        return ModerationResult.builder()
                .spam(spam)
                .toxic(toxic)
                .inappropriate(inappropriate)
                .suspiciousRepeat(repeated)
                .reasoning(reasoning.toString().trim())
                .build();
    }

    private boolean isRepeated(String text, Long authorUserId) {
        if (authorUserId == null) return false;
        LocalDateTime cutoff = LocalDateTime.now().minusDays(LOOKBACK_DAYS);

        List<String> recentPostBodies = postRepository
                .search(com.gehu.safety.entity.ModerationStatus.PUBLISHED, null, null,
                        PageRequest.of(0, RECENT_ITEMS_TO_CHECK, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent().stream()
                .filter(p -> p.getAuthor() != null && authorUserId.equals(p.getAuthor().getId())
                        && p.getCreatedAt().isAfter(cutoff))
                .map(p -> p.getTitle() + " " + p.getBody())
                .collect(Collectors.toList());

        if (RepeatedContentDetector.isNearDuplicate(text, recentPostBodies)) {
            return true;
        }

        List<String> recentCommentBodies = commentRepository
                .findAll(PageRequest.of(0, RECENT_ITEMS_TO_CHECK, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent().stream()
                .filter(c -> c.getAuthor() != null && authorUserId.equals(c.getAuthor().getId())
                        && c.getCreatedAt().isAfter(cutoff))
                .map(com.gehu.safety.entity.Comment::getBody)
                .collect(Collectors.toList());

        return RepeatedContentDetector.isNearDuplicate(text, recentCommentBodies);
    }
}
