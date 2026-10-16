package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trusted_contacts")
@Getter
@Setter
public class TrustedContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "relation", length = 50)
    private String relation;

    @Column(name = "priority", nullable = false)
    private Integer priority = 1;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public TrustedContact(Long id, User user, String name, String phone, String email, String relation, Integer priority, boolean verified, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.relation = relation;
        this.priority = priority;
        this.verified = verified;
        this.createdAt = createdAt;
    }

    public TrustedContact() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final TrustedContact value = new TrustedContact();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder user(User value) { this.value.user = value; return this; }
        public Builder name(String value) { this.value.name = value; return this; }
        public Builder phone(String value) { this.value.phone = value; return this; }
        public Builder email(String value) { this.value.email = value; return this; }
        public Builder relation(String value) { this.value.relation = value; return this; }
        public Builder priority(Integer value) { this.value.priority = value; return this; }
        public Builder verified(boolean value) { this.value.verified = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public TrustedContact build() { return value; }
    }
}
