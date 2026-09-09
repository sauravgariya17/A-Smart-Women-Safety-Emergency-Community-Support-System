package com.gehu.safety.dto.response;

import com.gehu.safety.entity.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Same anonymity guarantee as PostSummaryResponse - see its javadoc. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponse {
    private Long id;
    private String anonymousHandle;
    private String title;
    private String body;             // full body, unlike the feed excerpt
    private PostCategory category;
    private String areaLabel;
    private Integer upvoteCount;
    private Integer downvoteCount;
    private Integer commentCount;
    private Integer myVote;
    private LocalDateTime createdAt;
}
