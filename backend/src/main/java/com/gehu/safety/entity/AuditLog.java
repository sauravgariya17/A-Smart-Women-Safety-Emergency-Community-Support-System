package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable by convention: application code only ever INSERTs here
 * (AuditLogRepository exposes no update/delete usage anywhere in the
 * service layer). Used to record every state-changing SOS action so
 * there's a reconstructible trail of who did what and when.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id", nullable = false)
    private User actor;

    @Column(name = "action_type", nullable = false, length = 80)
    private String actionType; // e.g. SOS_TRIGGERED, SOS_ACKNOWLEDGED, SOS_RESOLVED

    @Column(name = "target_type", length = 40)
    private String targetType; // e.g. "SOS_ALERT"

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(Long id, User actor, Long targetId, String reason, LocalDateTime timestamp) {
        this.id = id;
        this.actor = actor;
        this.targetId = targetId;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public AuditLog() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AuditLog value = new AuditLog();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder actor(User value) { this.value.actor = value; return this; }
        public Builder actionType(String value) { this.value.actionType = value; return this; }
        public Builder targetType(String value) { this.value.targetType = value; return this; }
        public Builder targetId(Long value) { this.value.targetId = value; return this; }
        public Builder reason(String value) { this.value.reason = value; return this; }
        public Builder timestamp(LocalDateTime value) { this.value.timestamp = value; return this; }
        public AuditLog build() { return value; }
    }
}
