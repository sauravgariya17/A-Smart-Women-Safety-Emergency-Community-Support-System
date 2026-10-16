package com.gehu.safety.config;

import com.gehu.safety.security.JwtAuthEntryPoint;
import com.gehu.safety.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize on controller/service methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // stateless JWT API, CSRF not applicable
            .cors(cors -> {}) // configured via CorsConfig bean
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/map/**").permitAll()
                .requestMatchers("/api/journeys/track/**").permitAll() // token IS the credential, see JourneyController

                // Moderator + Admin - MUST be declared before the generic
                // /api/admin/** catch-all below, since Spring Security's
                // authorizeHttpRequests matches in order and the first match
                // wins. (Found during integration review: the old broad rule
                // was shadowing these, silently forcing them to ADMIN-only.)
                .requestMatchers("/api/moderation/**").hasAnyRole("MODERATOR", "ADMIN")
                .requestMatchers("/api/admin/incidents/**").hasAnyRole("MODERATOR", "ADMIN")
                .requestMatchers("/api/admin/dashboard/**").hasAnyRole("MODERATOR", "ADMIN")
                .requestMatchers("/api/analytics/**").hasAnyRole("MODERATOR", "ADMIN")

                // Admin-only, more specific than the catch-all but listed here for clarity
                .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/audit-logs/**").hasRole("ADMIN")

                // Safe default for any future /api/admin/** path not explicitly
                // listed above: require ADMIN rather than silently falling
                // through to "any authenticated user".
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Security staff + Admin
                .requestMatchers("/api/sos/manage/**").hasAnyRole("SECURITY_STAFF", "ADMIN")

                // Everything else requires a valid authenticated user
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
