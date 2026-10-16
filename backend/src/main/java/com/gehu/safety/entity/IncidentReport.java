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
    private IncidentVisibility visibilityType = IncidentVisibility.ANON_PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
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

    public IncidentReport(Long id, User user, String description, IncidentCategory category, PriorityLevel priority, IncidentCategory aiSuggestedCategory, PriorityLevel aiSuggestedPriority, Double aiConfidence, String aiReasoning, String aiSummary, Double lat, Double lng, String areaLabel, String evidenceUrl, IncidentVisibility visibilityType, IncidentStatus status, LocalDateTime createdAt, User reviewedBy, LocalDateTime reviewedAt) {
        this.id = id;
        this.user = user;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.aiSuggestedCategory = aiSuggestedCategory;
        this.aiSuggestedPriority = aiSuggestedPriority;
        this.aiConfidence = aiConfidence;
        this.aiReasoning = aiReasoning;
        this.aiSummary = aiSummary;
        this.lat = lat;
        this.lng = lng;
        this.areaLabel = areaLabel;
        this.evidenceUrl = evidenceUrl;
        this.visibilityType = visibilityType;
        this.status = status;
        this.createdAt = createdAt;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
    }

    public IncidentReport() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final IncidentReport value = new IncidentReport();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder user(User value) { this.value.user = value; return this; }
        public Builder description(String value) { this.value.description = value; return this; }
        public Builder category(IncidentCategory value) { this.value.category = value; return this; }
        public Builder priority(PriorityLevel value) { this.value.priority = value; return this; }
        public Builder aiSuggestedCategory(IncidentCategory value) { this.value.aiSuggestedCategory = value; return this; }
        public Builder aiSuggestedPriority(PriorityLevel value) { this.value.aiSuggestedPriority = value; return this; }
        public Builder aiConfidence(Double value) { this.value.aiConfidence = value; return this; }
        public Builder aiReasoning(String value) { this.value.aiReasoning = value; return this; }
        public Builder aiSummary(String value) { this.value.aiSummary = value; return this; }
        public Builder lat(Double value) { this.value.lat = value; return this; }
        public Builder lng(Double value) { this.value.lng = value; return this; }
        public Builder areaLabel(String value) { this.value.areaLabel = value; return this; }
        public Builder evidenceUrl(String value) { this.value.evidenceUrl = value; return this; }
        public Builder visibilityType(IncidentVisibility value) { this.value.visibilityType = value; return this; }
        public Builder status(IncidentStatus value) { this.value.status = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public Builder assignedTo(User value) { this.value.assignedTo = value; return this; }
        public Builder reviewedBy(User value) { this.value.reviewedBy = value; return this; }
        public Builder reviewedAt(LocalDateTime value) { this.value.reviewedAt = value; return this; }
        public IncidentReport build() { return value; }
    }
}
