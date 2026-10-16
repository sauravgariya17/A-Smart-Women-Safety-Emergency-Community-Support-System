package com.gehu.safety.service;

public interface AuditService {
    void record(Long actorUserId, String actionType, String targetType, Long targetId, String reason);
}
