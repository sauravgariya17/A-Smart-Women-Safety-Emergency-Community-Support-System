package com.gehu.safety.controller;

import com.gehu.safety.dto.request.ResolveSosRequest;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.SosManagementResponse;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.SosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Restricted to SECURITY_STAFF/ADMIN both here (@PreAuthorize) and in
 * SecurityConfig's route rules (/api/sos/manage/** -> hasAnyRole). This is
 * a campus security dashboard, NOT an integration with police or any
 * external emergency-service API - none exists in this prototype.
 */
@RestController
@RequestMapping("/api/sos/manage")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SECURITY_STAFF','ADMIN')")
public class SosManagementController {

    private final SosService sosService;

    @GetMapping("/active")
    public ApiResponse<List<SosManagementResponse>> getActiveQueue() {
        return ApiResponse.success(sosService.getActiveQueue());
    }

    @PostMapping("/{sosId}/acknowledge")
    public ApiResponse<SosManagementResponse> acknowledge(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long sosId) {

        return ApiResponse.success("Alert acknowledged", sosService.acknowledge(principal.getId(), sosId));
    }

    @PostMapping("/{sosId}/resolve")
    public ApiResponse<SosManagementResponse> resolve(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long sosId,
            @Valid @RequestBody ResolveSosRequest request) {

        return ApiResponse.success("SOS event closed", sosService.resolve(principal.getId(), sosId, request));
    }
}
