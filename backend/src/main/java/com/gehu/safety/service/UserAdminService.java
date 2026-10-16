package com.gehu.safety.service;

import com.gehu.safety.dto.response.AdminUserResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.entity.Role;

public interface UserAdminService {
    PageResponse<AdminUserResponse> listUsers(Role role, String keyword, int page, int size);
    AdminUserResponse updateRole(Long actorUserId, Long targetUserId, Role newRole);
    AdminUserResponse updateStatus(Long actorUserId, Long targetUserId, boolean active);
}
