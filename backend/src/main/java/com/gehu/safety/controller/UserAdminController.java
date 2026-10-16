package com.gehu.safety.controller;

import com.gehu.safety.dto.request.UpdateUserRoleRequest;
import com.gehu.safety.dto.request.UpdateUserStatusRequest;
import com.gehu.safety.dto.response.AdminUserResponse;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.entity.Role;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.UserAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * ADMIN ONLY - not MODERATOR. Role changes and account suspension are the
 * most sensitive actions in the system; MODERATOR gets content moderation
 * and incident triage (see ModerationController, IncidentAdminController)
 * but not control over other people's accounts.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping
    public ApiResponse<PageResponse<AdminUserResponse>> listUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ApiResponse.success(userAdminService.listUsers(role, keyword, page, size));
    }

    @PatchMapping("/{userId}/role")
    public ApiResponse<AdminUserResponse> updateRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        return ApiResponse.success("Role updated", userAdminService.updateRole(principal.getId(), userId, request.getRole()));
    }

    @PatchMapping("/{userId}/status")
    public ApiResponse<AdminUserResponse> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        return ApiResponse.success(
                request.getActive() ? "User activated" : "User suspended",
                userAdminService.updateStatus(principal.getId(), userId, request.getActive()));
    }
}
