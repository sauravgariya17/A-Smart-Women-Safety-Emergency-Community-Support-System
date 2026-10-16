package com.gehu.safety.dto.response;

import com.gehu.safety.entity.Role;
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

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String anonymousHandle;
    private Role role;
    private boolean verified;

    public UserResponse(Long id, String name, String email, String anonymousHandle, Role role, boolean verified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.anonymousHandle = anonymousHandle;
        this.role = role;
        this.verified = verified;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final UserResponse value = new UserResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder name(String value) { this.value.name = value; return this; }
        public Builder email(String value) { this.value.email = value; return this; }
        public Builder anonymousHandle(String value) { this.value.anonymousHandle = value; return this; }
        public Builder role(Role value) { this.value.role = value; return this; }
        public Builder verified(boolean value) { this.value.verified = value; return this; }
        public UserResponse build() { return value; }
    }
}
