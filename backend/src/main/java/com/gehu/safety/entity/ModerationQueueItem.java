package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * `reporterUser` is internal-only (anti-abuse / duplicate-report tracking).
 * It must never be shown to the moderator reviewing the queue - moderators
 * see WHAT was reported and WHY, not WHO reported it, to prevent retaliation
 * and to keep the review focused on the content itself.
 */
@Entity
@Table(name = "moderation_queue")
@Getter
@Setter
public class ModerationQueueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "reason", length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "flagged_by", nullable = false, length = 10)
    private ReportSource flaggedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id")
    private User reporterUser; // INTERNAL ONLY - never exposed to moderators via DTO

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ModerationQueueStatus status = ModerationQueueStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public ModerationQueueItem(Long id, TargetType targetType, Long targetId, String reason, ReportSource flaggedBy, ModerationQueueStatus status, User reviewedBy, LocalDateTime reviewedAt, LocalDateTime createdAt) {
        this.id = id;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.flaggedBy = flaggedBy;
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.createdAt = createdAt;
    }

    public ModerationQueueItem() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ModerationQueueItem value = new ModerationQueueItem();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder targetType(TargetType value) { this.value.targetType = value; return this; }
        public Builder targetId(Long value) { this.value.targetId = value; return this; }
        public Builder reason(String value) { this.value.reason = value; return this; }
        public Builder flaggedBy(ReportSource value) { this.value.flaggedBy = value; return this; }
        public Builder reporterUser(User value) { this.value.reporterUser = value; return this; }
        public Builder status(ModerationQueueStatus value) { this.value.status = value; return this; }
        public Builder reviewedBy(User value) { this.value.reviewedBy = value; return this; }
        public Builder reviewedAt(LocalDateTime value) { this.value.reviewedAt = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public ModerationQueueItem build() { return value; }
    }
}
