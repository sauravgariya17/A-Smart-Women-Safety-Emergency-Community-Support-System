package com.gehu.safety.service.impl;

import com.gehu.safety.dto.request.LoginRequest;
import com.gehu.safety.dto.request.RegisterRequest;
import com.gehu.safety.dto.response.AuthResponse;
import com.gehu.safety.dto.response.UserResponse;
import com.gehu.safety.entity.RefreshToken;
import com.gehu.safety.entity.Role;
import com.gehu.safety.entity.User;
import com.gehu.safety.exception.BadRequestException;
import com.gehu.safety.exception.DuplicateResourceException;
import com.gehu.safety.repository.RefreshTokenRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.security.JwtTokenProvider;
import com.gehu.safety.security.JwtProperties;
import com.gehu.safety.service.AnonymousHandleGenerator;
import com.gehu.safety.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final AnonymousHandleGenerator handleGenerator;


    public AuthServiceImpl(UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            JwtProperties jwtProperties,
            AnonymousHandleGenerator handleGenerator) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.handleGenerator = handleGenerator;
    }
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneEncrypted(request.getPhone()) // TODO Stage 2: wrap with AES AttributeConverter
                .role(Role.USER)
                .anonymousHandle(handleGenerator.generateUnique())
                .verified(false) // TODO Stage 2: wire up email OTP verification
                .active(true)
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(String rawRefreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired or revoked. Please log in again.");
        }

        User user = stored.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(rawRefreshToken) // rotation optional-extend for stage 2: issue a new one and revoke old
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByToken(rawRefreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String rawRefreshToken = jwtTokenProvider.generateRawRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(rawRefreshToken)
                .expiresAt(LocalDateTime.now().plusNanos(jwtProperties.getRefreshTokenExpiryMs() * 1_000_000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User user) {
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
