package com.gehu.safety.dto.response;

import com.gehu.safety.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Admin/moderator view. Identity is shown ONLY when visibilityType allows it
 * (NAMED_ADMIN_ONLY) - for ANON_PUBLIC/FULL_ANON reports, reporterName is
 * null even to admins, same anonymity-boundary discipline as Community.
 * aiSummary is included here specifically because it's an admin-only feature.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentAdminResponse {
    private Long id;
    private String reporterName; // null unless visibilityType == NAMED_ADMIN_ONLY
    private String description;
    private String aiSummary;
    private IncidentCategory category;
    private PriorityLevel priority;
    private IncidentCategory aiSuggestedCategory;
    private PriorityLevel aiSuggestedPriority;
    private Double aiConfidence;
    private String aiReasoning;
    private String assignedToName;
    private Double lat;
    private Double lng;
    private String areaLabel;
    private IncidentVisibility visibilityType;
    private IncidentStatus status;
    private LocalDateTime createdAt;
}
