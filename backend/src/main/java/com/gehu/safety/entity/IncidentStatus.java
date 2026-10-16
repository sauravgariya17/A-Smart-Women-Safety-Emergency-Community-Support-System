package com.gehu.safety.entity;

/**
 * Full triage pipeline:
 *   SUBMITTED -> UNDER_REVIEW -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
 * Not strictly enforced as a linear state machine (an admin can jump stages
 * or move backward, e.g. re-opening a closed report) - unlike SOS/Journey,
 * incident triage is a human workflow where flexibility matters more than
 * rigid transitions. Every change is still audit-logged.
 */
public enum IncidentStatus {
    SUBMITTED,
    UNDER_REVIEW,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
