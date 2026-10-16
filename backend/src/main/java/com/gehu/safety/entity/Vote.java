package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(name = "uq_vote_once", columnNames = {"user_id", "target_type", "target_id"})
})
@Getter
@Setter
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "vote_value", nullable = false)
    private Integer voteValue; // +1 or -1

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Vote(Long id, User user, TargetType targetType, Long targetId, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.targetType = targetType;
        this.targetId = targetId;
        this.createdAt = createdAt;
    }

    public Vote() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Vote value = new Vote();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder user(User value) { this.value.user = value; return this; }
        public Builder targetType(TargetType value) { this.value.targetType = value; return this; }
        public Builder targetId(Long value) { this.value.targetId = value; return this; }
        public Builder voteValue(Integer value) { this.value.voteValue = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public Vote build() { return value; }
    }
}
