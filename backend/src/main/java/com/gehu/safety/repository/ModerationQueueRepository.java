package com.gehu.safety.repository;

import com.gehu.safety.entity.ModerationQueueItem;
import com.gehu.safety.entity.ModerationQueueStatus;
import com.gehu.safety.entity.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationQueueRepository extends JpaRepository<ModerationQueueItem, Long> {
    Page<ModerationQueueItem> findByStatus(ModerationQueueStatus status, Pageable pageable);
    boolean existsByTargetTypeAndTargetIdAndReporterUserId(TargetType targetType, Long targetId, Long reporterUserId);
}
