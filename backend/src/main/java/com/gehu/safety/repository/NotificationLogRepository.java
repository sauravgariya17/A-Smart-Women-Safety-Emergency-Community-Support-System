package com.gehu.safety.repository;

import com.gehu.safety.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findBySosAlertIdOrderBySentAtAsc(Long sosId);
}
