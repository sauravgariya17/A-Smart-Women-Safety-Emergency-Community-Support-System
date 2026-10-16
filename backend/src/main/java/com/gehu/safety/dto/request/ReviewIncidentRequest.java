package com.gehu.safety.dto.request;

import com.gehu.safety.entity.IncidentCategory;
import com.gehu.safety.entity.IncidentStatus;
import com.gehu.safety.entity.PriorityLevel;
import lombok.Data;

/** All fields optional - admin/moderator sets only what they want to change. */
@Data
public class ReviewIncidentRequest {
    private IncidentCategory category;   // override the AI/reporter category if needed
    private PriorityLevel priority;      // override the AI-suggested priority
    private IncidentStatus status;
    private Long assignedToUserId;       // staff member to assign this report to (moves status toward ASSIGNED)
}
