package com.gehu.safety.controller;

import com.gehu.safety.dto.request.ReviewIncidentRequest;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.IncidentAdminResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.entity.IncidentCategory;
import com.gehu.safety.entity.IncidentStatus;
import com.gehu.safety.entity.PriorityLevel;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/incidents")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
public class IncidentAdminController {

    private final IncidentService incidentService;

    @GetMapping
    public ApiResponse<PageResponse<IncidentAdminResponse>> getAll(
            @RequestParam(required = false) IncidentCategory category,
            @RequestParam(required = false) PriorityLevel priority,
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ApiResponse.success(incidentService.getAllIncidents(category, priority, status, page, size));
    }

    @PatchMapping("/{incidentId}/review")
    public ApiResponse<IncidentAdminResponse> review(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long incidentId,
            @Valid @RequestBody ReviewIncidentRequest request) {

        return ApiResponse.success("Incident updated", incidentService.reviewIncident(principal.getId(), incidentId, request));
    }
}
