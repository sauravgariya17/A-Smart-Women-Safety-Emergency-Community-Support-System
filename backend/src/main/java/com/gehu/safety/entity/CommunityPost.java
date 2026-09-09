package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ============================================================
 * ANONYMITY BOUNDARY - READ BEFORE MODIFYING THIS ENTITY
 * ============================================================
 * `authorUserId` (via the `user` relation below) is the ONLY link between
 * this post and a real account. It exists strictly for:
 *   - moderation (a moderator resolving a report needs SOME way to act)
 *   - anti-abuse (rate limiting, ban enforcement)
 *   - a future audited ADMIN-only identity-reveal action
 *
 * It must NEVER be read into a response DTO. The DTOs in dto/response
 * (PostSummaryResponse, PostDetailResponse) do not even declare a field
 * for it, so leaking it would require deliberately changing the DTO
 * class itself - not just a careless mapping line in a service.
 *
 * `anonymousHandleSnapshot` is what every other user actually sees. It is
 * copied from User.anonymousHandle at creation time so that even if the
 * author later regenerates their handle, old posts stay consistent and
 * still cannot be correlated to the live handle.
 */
@Entity
@Table(name = "community_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author; // INTERNAL ONLY - see class javadoc

    @Column(name = "anonymous_handle_snapshot", nullable = false, length = 50)
    private String anonymousHandleSnapshot;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", nullable = false, length = 5000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag", length = 40)
    private PostCategory category;

    /**
     * Coarse, human-chosen area label only (e.g. "Near Main Gate",
     * "Library Block"). Never store or accept exact GPS coordinates here -
     * that belongs to IncidentReports/SOS, which have their own stricter
     * access control, not the public community feed.
     */
    @Column(name = "area_label", length = 120)
    private String areaLabel;

    @Column(name = "ai_toxicity_score")
    private Double aiToxicityScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, length = 20)
    @Builder.Default
    private ModerationStatus moderationStatus = ModerationStatus.PUBLISHED;

    @Column(name = "upvote_count", nullable = false)
    @Builder.Default
    private Integer upvoteCount = 0;

    @Column(name = "downvote_count", nullable = false)
    @Builder.Default
    private Integer downvoteCount = 0;

    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
