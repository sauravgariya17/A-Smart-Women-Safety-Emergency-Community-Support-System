package com.gehu.safety.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Rule-based placeholder for the AI moderation microservice described in
 * the project SRS. It gives the moderation workflow something real to do
 * NOW without requiring the Python service to exist yet.
 *
 * Swap this out later: AiModerationClient (calling the Python service) can
 * implement the same simple contract - `checkContent(text) -> flagged?` -
 * so CommunityPostService/CommentService do not need to change at all.
 */
@Component
public class ContentModerationChecker {

    // Deliberately short, illustrative list - not exhaustive. Replace with
    // the trained AI classifier in a later stage.
    private static final List<String> FLAGGED_KEYWORDS = List.of(
            "kill", "rape", "suicide", "murder", "attack her", "stalk her home"
    );

    /**
     * @return a flagging reason string if the content should go to the
     *         moderation queue, or null if it looks fine to auto-publish.
     */
    public String checkContent(String text) {
        if (text == null) return null;
        String lower = text.toLowerCase(Locale.ROOT);
        for (String keyword : FLAGGED_KEYWORDS) {
            if (lower.contains(keyword)) {
                return "Automated keyword flag: contains potentially severe content";
            }
        }
        return null;
    }
}
