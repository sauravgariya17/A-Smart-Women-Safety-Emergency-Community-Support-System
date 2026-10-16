package com.gehu.safety.service;

import com.gehu.safety.dto.response.AuditLogResponse;
import com.gehu.safety.dto.response.PageResponse;

public interface AuditQueryService {
    PageResponse<AuditLogResponse> search(String actionType, int page, int size);
}
