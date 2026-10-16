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
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
