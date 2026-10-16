package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Same anonymity boundary rules as CommunityPost - see that class's javadoc.
 * `author` is internal-only; `anonymousHandleSnapshot` is what's public.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author; // INTERNAL ONLY

    @Column(name = "anonymous_handle_snapshot", nullable = false, length = 50)
    private String anonymousHandleSnapshot;

    @Column(name = "body", nullable = false, length = 2000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, length = 20)
    private ModerationStatus moderationStatus = ModerationStatus.PUBLISHED;

    @Column(name = "upvote_count", nullable = false)
    private Integer upvoteCount = 0;

    @Column(name = "downvote_count", nullable = false)
    private Integer downvoteCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Comment(Long id, CommunityPost post, String anonymousHandleSnapshot, String body, ModerationStatus moderationStatus, Integer upvoteCount, Integer downvoteCount, LocalDateTime createdAt) {
        this.id = id;
        this.post = post;
        this.anonymousHandleSnapshot = anonymousHandleSnapshot;
        this.body = body;
        this.moderationStatus = moderationStatus;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.createdAt = createdAt;
    }

    public Comment() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Comment value = new Comment();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder post(CommunityPost value) { this.value.post = value; return this; }
        public Builder author(User value) { this.value.author = value; return this; }
        public Builder anonymousHandleSnapshot(String value) { this.value.anonymousHandleSnapshot = value; return this; }
        public Builder body(String value) { this.value.body = value; return this; }
        public Builder moderationStatus(ModerationStatus value) { this.value.moderationStatus = value; return this; }
        public Builder upvoteCount(Integer value) { this.value.upvoteCount = value; return this; }
        public Builder downvoteCount(Integer value) { this.value.downvoteCount = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public Comment build() { return value; }
    }
}
