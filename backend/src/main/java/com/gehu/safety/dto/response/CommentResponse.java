package com.gehu.safety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Same anonymity guarantee as PostSummaryResponse - no author identity field exists. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String anonymousHandle;
    private String body;
    private Integer upvoteCount;
    private Integer downvoteCount;
    private Integer myVote;
    private LocalDateTime createdAt;
}
