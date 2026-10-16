package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * user is ALWAYS populated internally (even for FULL_ANON reports) - same
 * anonymity-boundary pattern as CommunityPost: identity known internally,
 * hidden externally via DTO mapping and visibility_type checks.
 * aiSuggestedCategory/aiSuggestedPriority are kept immutable next to the
 * editable category/priority fields so we can audit how often humans
 * override the AI - see IncidentServiceImpl.
 */
@Entity
@Table(name = "incident_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "description", nullable = false, length = 3000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private IncidentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private PriorityLevel priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_suggested_category", length = 30)
    private IncidentCategory aiSuggestedCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_suggested_priority", length = 20)
    private PriorityLevel aiSuggestedPriority;

    @Column(name = "ai_confidence")
    private Double aiConfidence;

    @Column(name = "ai_reasoning", length = 500)
    private String aiReasoning;

    @Column(name = "ai_summary", length = 800)
    private String aiSummary;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Column(name = "area_label", length = 120)
    private String areaLabel;

    @Column(name = "evidence_url", length = 500)
    private String evidenceUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_type", nullable = false, length = 20)
    @Builder.Default
    private IncidentVisibility visibilityType = IncidentVisibility.ANON_PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.SUBMITTED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo; // which staff member (MODERATOR/ADMIN) is handling this, for the ASSIGNED stage

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
