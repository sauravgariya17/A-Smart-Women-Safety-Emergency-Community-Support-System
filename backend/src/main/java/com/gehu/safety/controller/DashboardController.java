package com.gehu.safety.controller;

import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.DashboardSummaryResponse;
import com.gehu.safety.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MODERATOR + ADMIN (not SECURITY_STAFF - they get SosManagementController's
 * focused SOS queue instead of the full cross-module dashboard).
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary(@RequestParam(defaultValue = "30") int windowDays) {
        return ApiResponse.success(dashboardService.getSummary(windowDays));
    }
}
