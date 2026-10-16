package com.gehu.safety.dto.response;

import com.gehu.safety.entity.Role;
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

public class AdminUserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean verified;
    private boolean active;
    private LocalDateTime createdAt;

    public AdminUserResponse(Long id, String name, String email, Role role, boolean verified, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.verified = verified;
        this.active = active;
        this.createdAt = createdAt;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AdminUserResponse value = new AdminUserResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder name(String value) { this.value.name = value; return this; }
        public Builder email(String value) { this.value.email = value; return this; }
        public Builder role(Role value) { this.value.role = value; return this; }
        public Builder verified(boolean value) { this.value.verified = value; return this; }
        public Builder active(boolean value) { this.value.active = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public AdminUserResponse build() { return value; }
    }
}
