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
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
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
    @Builder.Default
    private Set<TrustedContact> notifiedContacts = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
    }
}
