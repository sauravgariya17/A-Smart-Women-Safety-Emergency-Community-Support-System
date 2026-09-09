package com.gehu.safety.dto.response;

import com.gehu.safety.entity.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ANONYMITY NOTE: this class deliberately has NO userId/email/phone field.
 * It is structurally impossible to serialize the author's real identity
 * through this DTO - there is nowhere to put it even if a service method
 * tried to. Only `anonymousHandle` identifies the author to other users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSummaryResponse {
    private Long id;
    private String anonymousHandle;
    private String title;
    private String excerpt;          // truncated body for the feed view
    private PostCategory category;
    private String areaLabel;
    private Integer upvoteCount;
    private Integer downvoteCount;
    private Integer commentCount;
    private Integer myVote;          // -1, 0, or 1 - the CALLER's own vote, not anyone else's
    private LocalDateTime createdAt;
}
