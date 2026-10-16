package com.gehu.safety.service.impl;

import com.gehu.safety.dto.response.AdminUserResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.entity.Role;
import com.gehu.safety.entity.User;
import com.gehu.safety.exception.BadRequestException;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.AuditService;
import com.gehu.safety.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final AuditService auditService;

    @Override
    public PageResponse<AdminUserResponse> listUsers(Role role, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var results = userRepository.search(role, (keyword != null && !keyword.isBlank()) ? keyword.trim() : null, pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    @Transactional
    public AdminUserResponse updateRole(Long actorUserId, Long targetUserId, Role newRole) {
        if (actorUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot change your own role. Ask another admin to do this.");
        }
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role oldRole = target.getRole();
        target.setRole(newRole);
        userRepository.save(target);

        auditService.record(actorUserId, "USER_ROLE_CHANGED", "USER", targetUserId, oldRole + " -> " + newRole);
        return toResponse(target);
    }

    @Override
    @Transactional
    public AdminUserResponse updateStatus(Long actorUserId, Long targetUserId, boolean active) {
        if (actorUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot suspend/activate your own account.");
        }
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        target.setActive(active);
        userRepository.save(target);

        auditService.record(actorUserId, active ? "USER_ACTIVATED" : "USER_SUSPENDED", "USER", targetUserId, null);
        return toResponse(target);
    }

    private AdminUserResponse toResponse(User u) {
        return AdminUserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .verified(u.isVerified())
                .active(u.isActive())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
