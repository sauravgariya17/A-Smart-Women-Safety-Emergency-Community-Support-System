package com.gehu.safety.dto.response;

import com.gehu.safety.entity.PostCategory;
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

    public PostSummaryResponse(Long id, String anonymousHandle, String title, PostCategory category, String areaLabel, Integer upvoteCount, Integer downvoteCount, Integer commentCount, LocalDateTime createdAt) {
        this.id = id;
        this.anonymousHandle = anonymousHandle;
        this.title = title;
        this.category = category;
        this.areaLabel = areaLabel;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PostSummaryResponse response = new PostSummaryResponse();

        public Builder id(Long value) { response.id = value; return this; }
        public Builder anonymousHandle(String value) { response.anonymousHandle = value; return this; }
        public Builder title(String value) { response.title = value; return this; }
        public Builder excerpt(String value) { response.excerpt = value; return this; }
        public Builder category(PostCategory value) { response.category = value; return this; }
        public Builder areaLabel(String value) { response.areaLabel = value; return this; }
        public Builder upvoteCount(Integer value) { response.upvoteCount = value; return this; }
        public Builder downvoteCount(Integer value) { response.downvoteCount = value; return this; }
        public Builder commentCount(Integer value) { response.commentCount = value; return this; }
        public Builder myVote(Integer value) { response.myVote = value; return this; }
        public Builder createdAt(LocalDateTime value) { response.createdAt = value; return this; }
        public PostSummaryResponse build() { return response; }
    }
}
