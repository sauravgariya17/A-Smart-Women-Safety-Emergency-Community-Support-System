package com.gehu.safety.service.impl;

import com.gehu.safety.entity.AuditLog;
import com.gehu.safety.entity.User;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.AuditLogRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;


    public AuditServiceImpl(AuditLogRepository auditLogRepository,
            UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public void record(Long actorUserId, String actionType, String targetType, Long targetId, String reason) {
        User actor = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AuditLog entry = AuditLog.builder()
                .actor(actor)
                .actionType(actionType)
                .targetType(targetType)
                .targetId(targetId)
                .reason(reason)
                .build();

        auditLogRepository.save(entry); // insert-only - no update/delete method is ever called on this repository
    }
}
