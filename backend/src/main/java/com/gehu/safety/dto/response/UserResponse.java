package com.gehu.safety.dto.response;

import com.gehu.safety.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IMPORTANT: This DTO includes `name`, which is safe ONLY when returned to
 * the account owner (e.g. GET /api/users/me) or to an ADMIN performing an
 * authorized identity-reveal. NEVER map a User entity to this DTO for any
 * endpoint that other regular users can call (e.g. community post authors).
 * Use PublicHandleResponse (anonymous_handle only) for anything public-facing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String anonymousHandle;
    private Role role;
    private boolean verified;
}
