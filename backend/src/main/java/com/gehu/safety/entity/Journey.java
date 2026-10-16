package com.gehu.safety.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * PRIVACY-BY-DESIGN NOTE:
 * This entity intentionally has NO location-history table behind it.
 * `lastKnownLat/Lng` + `lastPingAt` are overwritten on every ping - there is
 * no trail of everywhere the user has been, only "where are they right now,
 * during this specific active journey." Once the journey ends (COMPLETED,
 * CANCELLED, or ESCALATED), no further location writes are accepted for it
 * (enforced in JourneyServiceImpl).
 */
@Entity
@Table(name = "journeys")
@Getter
@Setter
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journey_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_lat", nullable = false)
    private Double startLat;

    @Column(name = "start_lng", nullable = false)
    private Double startLng;

    @Column(name = "dest_lat", nullable = false)
    private Double destLat;

    @Column(name = "dest_lng", nullable = false)
    private Double destLng;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "expected_arrival", nullable = false)
    private LocalDateTime expectedArrival;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JourneyStatus status = JourneyStatus.ACTIVE;

    @Column(name = "last_known_lat")
    private Double lastKnownLat;

    @Column(name = "last_known_lng")
    private Double lastKnownLng;

    @Column(name = "last_ping_at")
    private LocalDateTime lastPingAt;

    /** Random, unguessable, shared only with the selected trusted contacts. */
    @Column(name = "tracking_token", nullable = false, unique = true, length = 100)
    private String trackingToken;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "journey_trusted_contacts",
            joinColumns = @JoinColumn(name = "journey_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private Set<TrustedContact> notifiedContacts = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
    }

    public Journey(Long id, User user, Double startLat, Double startLng, Double destLat, Double destLng, LocalDateTime startTime, LocalDateTime expectedArrival, JourneyStatus status, Double lastKnownLat, Double lastKnownLng, LocalDateTime lastPingAt, String trackingToken, Set<TrustedContact> notifiedContacts) {
        this.id = id;
        this.user = user;
        this.startLat = startLat;
        this.startLng = startLng;
        this.destLat = destLat;
        this.destLng = destLng;
        this.startTime = startTime;
        this.expectedArrival = expectedArrival;
        this.status = status;
        this.lastKnownLat = lastKnownLat;
        this.lastKnownLng = lastKnownLng;
        this.lastPingAt = lastPingAt;
        this.trackingToken = trackingToken;
        this.notifiedContacts = notifiedContacts;
    }

    public Journey() {}

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Journey value = new Journey();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder user(User value) { this.value.user = value; return this; }
        public Builder startLat(Double value) { this.value.startLat = value; return this; }
        public Builder startLng(Double value) { this.value.startLng = value; return this; }
        public Builder destLat(Double value) { this.value.destLat = value; return this; }
        public Builder destLng(Double value) { this.value.destLng = value; return this; }
        public Builder startTime(LocalDateTime value) { this.value.startTime = value; return this; }
        public Builder expectedArrival(LocalDateTime value) { this.value.expectedArrival = value; return this; }
        public Builder status(JourneyStatus value) { this.value.status = value; return this; }
        public Builder lastKnownLat(Double value) { this.value.lastKnownLat = value; return this; }
        public Builder lastKnownLng(Double value) { this.value.lastKnownLng = value; return this; }
        public Builder lastPingAt(LocalDateTime value) { this.value.lastPingAt = value; return this; }
        public Builder trackingToken(String value) { this.value.trackingToken = value; return this; }
        public Builder notifiedContacts(Set<TrustedContact> value) { this.value.notifiedContacts = value; return this; }
        public Journey build() { return value; }
    }
}
