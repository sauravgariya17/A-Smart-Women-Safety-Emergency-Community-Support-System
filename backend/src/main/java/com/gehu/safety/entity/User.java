package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Core identity table.
 *
 * PRIVACY NOTE: `name` and `phoneEncrypted` must NEVER be serialized into any
 * public-facing DTO (community posts, comments, votes). Only `anonymousHandle`
 * is safe to expose publicly. See CommunityPost/Comment entities for how the
 * anonymity boundary is enforced at write-time as well.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "phone_encrypted")
    private String phoneEncrypted;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(name = "anonymous_handle", nullable = false, unique = true, length = 50)
    private String anonymousHandle;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User(Long id, String name, String email, String passwordHash, String phoneEncrypted, Role role, String anonymousHandle, boolean verified, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneEncrypted = phoneEncrypted;
        this.role = role;
        this.anonymousHandle = anonymousHandle;
        this.verified = verified;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final User value = new User();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder name(String value) { this.value.name = value; return this; }
        public Builder email(String value) { this.value.email = value; return this; }
        public Builder passwordHash(String value) { this.value.passwordHash = value; return this; }
        public Builder phoneEncrypted(String value) { this.value.phoneEncrypted = value; return this; }
        public Builder role(Role value) { this.value.role = value; return this; }
        public Builder anonymousHandle(String value) { this.value.anonymousHandle = value; return this; }
        public Builder verified(boolean value) { this.value.verified = value; return this; }
        public Builder active(boolean value) { this.value.active = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public Builder updatedAt(LocalDateTime value) { this.value.updatedAt = value; return this; }
        public User build() { return value; }
    }
}
