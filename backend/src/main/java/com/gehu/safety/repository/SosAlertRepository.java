package com.gehu.safety.repository;

import com.gehu.safety.entity.SosAlert;
import com.gehu.safety.entity.SosStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SosAlertRepository extends JpaRepository<SosAlert, Long> {

    Optional<SosAlert> findByIdAndUserId(Long id, Long userId);

    List<SosAlert> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** A user should only ever have one non-terminal SOS at a time. */
    Optional<SosAlert> findFirstByUserIdAndStatusIn(Long userId, List<SosStatus> statuses);

    /** Security/admin dashboard feed. */
    List<SosAlert> findByStatusInOrderByCreatedAtAsc(List<SosStatus> statuses);

    /** Used by the scheduled auto-cancel sweep. */
    List<SosAlert> findByStatusAndConfirmationExpiresAtBefore(SosStatus status, LocalDateTime cutoff);

    long countByStatusIn(List<SosStatus> statuses);
}
