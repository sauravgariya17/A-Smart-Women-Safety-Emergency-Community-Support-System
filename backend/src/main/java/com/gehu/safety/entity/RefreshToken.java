package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Server-side record of issued refresh tokens.
 * Storing these (instead of pure stateless refresh tokens) lets us revoke a
 * session immediately - e.g. when an admin suspends an account.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public RefreshToken(Long id, User user, String token, LocalDateTime expiresAt, boolean revoked, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public RefreshToken() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final RefreshToken value = new RefreshToken();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder user(User value) { this.value.user = value; return this; }
        public Builder token(String value) { this.value.token = value; return this; }
        public Builder expiresAt(LocalDateTime value) { this.value.expiresAt = value; return this; }
        public Builder revoked(boolean value) { this.value.revoked = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public RefreshToken build() { return value; }
    }
}
