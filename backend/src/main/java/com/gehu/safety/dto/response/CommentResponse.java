package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Same anonymity guarantee as PostSummaryResponse - no author identity field exists. */
@Data
@NoArgsConstructor

public class CommentResponse {
    private Long id;
    private String anonymousHandle;
    private String body;
    private Integer upvoteCount;
    private Integer downvoteCount;
    private Integer myVote;
    private LocalDateTime createdAt;

    public CommentResponse(Long id, String anonymousHandle, String body, Integer upvoteCount, Integer downvoteCount, Integer myVote, LocalDateTime createdAt) {
        this.id = id;
        this.anonymousHandle = anonymousHandle;
        this.body = body;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.myVote = myVote;
        this.createdAt = createdAt;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final CommentResponse value = new CommentResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder anonymousHandle(String value) { this.value.anonymousHandle = value; return this; }
        public Builder body(String value) { this.value.body = value; return this; }
        public Builder upvoteCount(Integer value) { this.value.upvoteCount = value; return this; }
        public Builder downvoteCount(Integer value) { this.value.downvoteCount = value; return this; }
        public Builder myVote(Integer value) { this.value.myVote = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public CommentResponse build() { return value; }
    }
}
