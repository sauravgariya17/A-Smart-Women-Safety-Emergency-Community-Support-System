package com.gehu.safety.service.impl;

import com.gehu.safety.dto.response.AuditLogResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.repository.AuditLogRepository;
import com.gehu.safety.service.AuditQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditQueryServiceImpl implements AuditQueryService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public PageResponse<AuditLogResponse> search(String actionType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var results = auditLogRepository.search(actionType, pageable);
        return PageResponse.from(results.map(a -> AuditLogResponse.builder()
                .id(a.getId())
                .actorName(a.getActor().getName())
                .actionType(a.getActionType())
                .targetType(a.getTargetType())
                .targetId(a.getTargetId())
                .reason(a.getReason())
                .timestamp(a.getTimestamp())
                .build()));
    }
}
