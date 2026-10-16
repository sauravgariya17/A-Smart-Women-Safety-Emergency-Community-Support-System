package com.gehu.safety.service;

import com.gehu.safety.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates a random, non-identifying handle at registration time, e.g. "SafeVoice_4821".
 * This is the ONLY name ever shown next to a community post or comment.
 * It carries no link to the user's real name, email, or roll number.
 */
@Component
public class AnonymousHandleGenerator {

    private static final String[] ADJECTIVES = {
        "Brave", "Silent", "Guardian", "SafeVoice", "Steady", "Quiet", "Firm", "Bold", "Calm", "Vigilant"
    };

    private final UserRepository userRepository;

    public AnonymousHandleGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    private final SecureRandom random = new SecureRandom();

    public String generateUnique() {
        String handle;
        do {
            String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
            int suffix = 1000 + random.nextInt(9000); // 4-digit random suffix
            handle = adjective + "_" + suffix;
        } while (userRepository.existsByAnonymousHandle(handle));
        return handle;
    }
}
