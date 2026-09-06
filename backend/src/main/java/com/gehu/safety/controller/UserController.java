package com.gehu.safety.controller;

import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.UserResponse;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Returns the caller's OWN full profile (includes real name).
     * This is safe because it only ever returns data about the authenticated
     * caller themself - never about any other user.
     */
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse response = userService.getCurrentUserProfile(principal.getId());
        return ApiResponse.success(response);
    }
}
