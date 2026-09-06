package com.gehu.safety.service;

import com.gehu.safety.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUserProfile(Long userId);
}
