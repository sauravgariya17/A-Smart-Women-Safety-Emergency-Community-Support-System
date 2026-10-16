package com.gehu.safety.dto.response;

import com.gehu.safety.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** The reporter's own view of their submitted report. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentResponse {
    private Long id;
    private String description;
    private IncidentCategory category;
    private PriorityLevel priority;
    private IncidentCategory aiSuggestedCategory;
    private PriorityLevel aiSuggestedPriority;
    private Double aiConfidence;
    private String areaLabel;
    private IncidentVisibility visibilityType;
    private IncidentStatus status;
    private LocalDateTime createdAt;
}
