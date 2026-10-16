package com.gehu.safety.dto.request;

import com.gehu.safety.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotNull(message = "role is required")
    private Role role;
}
