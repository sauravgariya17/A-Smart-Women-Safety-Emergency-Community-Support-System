package com.gehu.safety.repository;

import com.gehu.safety.entity.IncidentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {

    List<IncidentReport> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<IncidentReport> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT r FROM IncidentReport r WHERE " +
           "(:category IS NULL OR r.category = :category) AND " +
           "(:priority IS NULL OR r.priority = :priority) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<IncidentReport> search(@Param("category") com.gehu.safety.entity.IncidentCategory category,
                                 @Param("priority") com.gehu.safety.entity.PriorityLevel priority,
                                 @Param("status") com.gehu.safety.entity.IncidentStatus status,
                                 Pageable pageable);

    /** Used by SafetyAnalyticsService - aggregated counts only, never individual rows returned to the API. */
    @Query("SELECT r.category, COUNT(r) FROM IncidentReport r WHERE r.createdAt >= :since GROUP BY r.category")
    List<Object[]> countByCategorySince(@Param("since") LocalDateTime since);

    @Query("SELECT r.areaLabel, COUNT(r) FROM IncidentReport r WHERE r.areaLabel IS NOT NULL AND r.createdAt >= :since GROUP BY r.areaLabel")
    List<Object[]> countByAreaSince(@Param("since") LocalDateTime since);

    @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r) FROM IncidentReport r WHERE r.createdAt >= :since GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
    List<Object[]> countByDaySince(@Param("since") LocalDateTime since);

    long countByStatusIn(List<com.gehu.safety.entity.IncidentStatus> statuses);
}
