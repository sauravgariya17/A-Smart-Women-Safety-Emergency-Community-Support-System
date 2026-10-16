package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType; // "Bearer"
    private UserResponse user;

    public AuthResponse(String accessToken, String refreshToken, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AuthResponse value = new AuthResponse();
        public Builder accessToken(String value) { this.value.accessToken = value; return this; }
        public Builder refreshToken(String value) { this.value.refreshToken = value; return this; }
        public Builder tokenType(String value) { this.value.tokenType = value; return this; }
        public Builder user(UserResponse value) { this.value.user = value; return this; }
        public AuthResponse build() { return value; }
    }
}
