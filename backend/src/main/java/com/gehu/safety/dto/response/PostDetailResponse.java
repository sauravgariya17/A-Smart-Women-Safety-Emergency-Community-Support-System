package com.gehu.safety.dto.response;

import com.gehu.safety.entity.PostCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Same anonymity guarantee as PostSummaryResponse - see its javadoc. */
@Data
@NoArgsConstructor

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

    public PostDetailResponse(Long id, String anonymousHandle, String title, PostCategory category, String areaLabel, Integer upvoteCount, Integer downvoteCount, Integer commentCount, Integer myVote, LocalDateTime createdAt) {
        this.id = id;
        this.anonymousHandle = anonymousHandle;
        this.title = title;
        this.category = category;
        this.areaLabel = areaLabel;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.commentCount = commentCount;
        this.myVote = myVote;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PostDetailResponse response = new PostDetailResponse();

        public Builder id(Long value) { response.id = value; return this; }
        public Builder anonymousHandle(String value) { response.anonymousHandle = value; return this; }
        public Builder title(String value) { response.title = value; return this; }
        public Builder body(String value) { response.body = value; return this; }
        public Builder category(PostCategory value) { response.category = value; return this; }
        public Builder areaLabel(String value) { response.areaLabel = value; return this; }
        public Builder upvoteCount(Integer value) { response.upvoteCount = value; return this; }
        public Builder downvoteCount(Integer value) { response.downvoteCount = value; return this; }
        public Builder commentCount(Integer value) { response.commentCount = value; return this; }
        public Builder myVote(Integer value) { response.myVote = value; return this; }
        public Builder createdAt(LocalDateTime value) { response.createdAt = value; return this; }
        public PostDetailResponse build() { return response; }
    }
}
