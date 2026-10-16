package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * This table IS the notification architecture, made auditable: every
 * attempt to reach a trusted contact about an SOS event gets a row here,
 * whether it succeeded, failed, or was only simulated. Nothing about
 * police/emergency-service dispatch is recorded here because no such
 * integration exists in this prototype.
 */
@Entity
@Table(name = "notification_logs")
@Getter
@Setter
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sos_id", nullable = false)
    private SosAlert sosAlert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private TrustedContact contact; // null when the recipient is campus security, not a trusted contact

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 30)
    private NotificationChannel channel;

    @Column(name = "recipient", nullable = false, length = 150)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationLogStatus status;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    public NotificationLog(Long id, SosAlert sosAlert, NotificationChannel channel, String recipient, NotificationLogStatus status, LocalDateTime sentAt) {
        this.id = id;
        this.sosAlert = sosAlert;
        this.channel = channel;
        this.recipient = recipient;
        this.status = status;
        this.sentAt = sentAt;
    }

    public NotificationLog() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final NotificationLog value = new NotificationLog();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder sosAlert(SosAlert value) { this.value.sosAlert = value; return this; }
        public Builder contact(TrustedContact value) { this.value.contact = value; return this; }
        public Builder channel(NotificationChannel value) { this.value.channel = value; return this; }
        public Builder recipient(String value) { this.value.recipient = value; return this; }
        public Builder status(NotificationLogStatus value) { this.value.status = value; return this; }
        public Builder sentAt(LocalDateTime value) { this.value.sentAt = value; return this; }
        public NotificationLog build() { return value; }
    }
}
