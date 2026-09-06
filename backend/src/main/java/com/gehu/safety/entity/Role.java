package com.gehu.safety.entity;

/**
 * Application-wide roles.
 * USER          - default role for every registered student/faculty member
 * MODERATOR     - reviews community/moderation queue
 * SECURITY_STAFF- receives and acknowledges SOS alerts
 * ADMIN         - full platform control, only role allowed to reveal identity behind an anonymous post
 */
public enum Role {
    USER,
    MODERATOR,
    SECURITY_STAFF,
    ADMIN
}
