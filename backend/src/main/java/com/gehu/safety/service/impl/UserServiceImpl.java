package com.gehu.safety.service.impl;

import com.gehu.safety.dto.response.UserResponse;
import com.gehu.safety.entity.User;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .anonymousHandle(user.getAnonymousHandle())
                .role(user.getRole())
                .verified(user.isVerified())
                .build();
    }
}
