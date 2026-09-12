package com.gehu.safety.repository;

import com.gehu.safety.entity.Journey;
import com.gehu.safety.entity.JourneyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {

    Optional<Journey> findByIdAndUserId(Long id, Long userId);

    Optional<Journey> findByTrackingToken(String trackingToken);

    List<Journey> findByUserIdOrderByStartTimeDesc(Long userId);

    /** Used only by JourneyEscalationService's scheduled sweep. */
    List<Journey> findByStatusAndExpectedArrivalBefore(JourneyStatus status, LocalDateTime cutoff);

    /** Enforces "one active journey at a time" per user. */
    Optional<Journey> findByUserIdAndStatus(Long userId, JourneyStatus status);
}
