package com.gehu.safety.repository;

import com.gehu.safety.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTargetTypeAndTargetIdOrderByTimestampAsc(String targetType, Long targetId);

    @Query("SELECT a FROM AuditLog a WHERE (:actionType IS NULL OR a.actionType = :actionType) ORDER BY a.timestamp DESC")
    Page<AuditLog> search(@Param("actionType") String actionType, Pageable pageable);
}
