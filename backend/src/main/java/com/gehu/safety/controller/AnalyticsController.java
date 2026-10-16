package com.gehu.safety.controller;

import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.SafetyAnalyticsResponse;
import com.gehu.safety.service.SafetyAnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Restricted to MODERATOR/ADMIN - even though the data is aggregated, who
 * gets to see campus-wide safety patterns is itself a decision worth
 * gatekeeping, not something to expose to every logged-in user.
 */
@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
public class AnalyticsController {

    private final SafetyAnalyticsService safetyAnalyticsService;


    public AnalyticsController(SafetyAnalyticsService safetyAnalyticsService) {
        this.safetyAnalyticsService = safetyAnalyticsService;
    }
    @GetMapping("/safety")
    public ApiResponse<SafetyAnalyticsResponse> getSafetyAnalytics(
            @RequestParam(defaultValue = "30") int windowDays) {

        return ApiResponse.success(safetyAnalyticsService.getAnalytics(windowDays));
    }
}
