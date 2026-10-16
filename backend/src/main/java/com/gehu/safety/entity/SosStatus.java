package com.gehu.safety.entity;

/**
 * State machine:
 *   PENDING_CONFIRMATION --(confirm within window)--> TRIGGERED
 *   PENDING_CONFIRMATION --(timeout / user cancel)---> CANCELLED
 *   TRIGGERED --(security/admin acknowledges)--------> ACKNOWLEDGED
 *   TRIGGERED/ACKNOWLEDGED --(user: "I'm safe")-------> FALSE_ALARM
 *   ACKNOWLEDGED --(security/admin closes)------------> RESOLVED or FALSE_ALARM
 *
 * Trusted contacts are notified ONLY on the PENDING_CONFIRMATION -> TRIGGERED
 * transition (see SosServiceImpl.confirm) - never at creation time. This is
 * what makes the confirmation step meaningful rather than cosmetic.
 */
public enum SosStatus {
    PENDING_CONFIRMATION,
    TRIGGERED,
    ACKNOWLEDGED,
    RESOLVED,
    FALSE_ALARM,
    CANCELLED
}
