package com.gehu.safety.controller;

import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.AuditLogResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.service.AuditQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * ADMIN ONLY. The audit trail records EVERYONE's sensitive actions
 * (including moderators' and security staff's) - only an admin should be
 * able to review it, otherwise it stops being an independent check.
 */
@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditQueryService auditQueryService;

    @GetMapping
    public ApiResponse<PageResponse<AuditLogResponse>> search(
            @RequestParam(required = false) String actionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        return ApiResponse.success(auditQueryService.search(actionType, page, size));
    }
}
