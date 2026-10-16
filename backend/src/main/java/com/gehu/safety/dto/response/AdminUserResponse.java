package com.gehu.safety.dto.response;

import com.gehu.safety.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Deliberately excludes phone number - account administration (role,
 * verification, suspension) doesn't need it, so it isn't included, even
 * for ADMIN. "Only authorized information should be accessible" applies
 * to what's necessary for the task, not just what the role technically could see.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean verified;
    private boolean active;
    private LocalDateTime createdAt;
}
