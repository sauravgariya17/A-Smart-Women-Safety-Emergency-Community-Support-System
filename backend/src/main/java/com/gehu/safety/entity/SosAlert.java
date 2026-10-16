package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sos_alerts")
@Getter
@Setter
public class SosAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sos_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private SosCategory category = SosCategory.PERSONAL_SAFETY;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private SosStatus status = SosStatus.PENDING_CONFIRMATION;

    @Column(name = "confirmation_expires_at")
    private LocalDateTime confirmationExpiresAt;

    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private User acknowledgedBy;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "notes", length = 1000)
    private String notes;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public SosAlert(Long id, User user, SosCategory category, Double lat, Double lng, SosStatus status, LocalDateTime confirmationExpiresAt, LocalDateTime triggeredAt, LocalDateTime createdAt, User acknowledgedBy, LocalDateTime acknowledgedAt, LocalDateTime resolvedAt, String notes) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.confirmationExpiresAt = confirmationExpiresAt;
        this.triggeredAt = triggeredAt;
        this.createdAt = createdAt;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = acknowledgedAt;
        this.resolvedAt = resolvedAt;
        this.notes = notes;
    }

    public SosAlert() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final SosAlert value = new SosAlert();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder user(User value) { this.value.user = value; return this; }
        public Builder category(SosCategory value) { this.value.category = value; return this; }
        public Builder lat(Double value) { this.value.lat = value; return this; }
        public Builder lng(Double value) { this.value.lng = value; return this; }
        public Builder status(SosStatus value) { this.value.status = value; return this; }
        public Builder confirmationExpiresAt(LocalDateTime value) { this.value.confirmationExpiresAt = value; return this; }
        public Builder triggeredAt(LocalDateTime value) { this.value.triggeredAt = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public Builder acknowledgedBy(User value) { this.value.acknowledgedBy = value; return this; }
        public Builder acknowledgedAt(LocalDateTime value) { this.value.acknowledgedAt = value; return this; }
        public Builder resolvedAt(LocalDateTime value) { this.value.resolvedAt = value; return this; }
        public Builder notes(String value) { this.value.notes = value; return this; }
        public SosAlert build() { return value; }
    }
}
