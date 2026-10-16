package com.gehu.safety.service;

import com.gehu.safety.dto.request.CreateIncidentAnalyzeRequest;
import com.gehu.safety.dto.request.CreateIncidentRequest;
import com.gehu.safety.dto.request.ReviewIncidentRequest;
import com.gehu.safety.dto.response.IncidentAdminResponse;
import com.gehu.safety.dto.response.IncidentAnalysisResponse;
import com.gehu.safety.dto.response.IncidentResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.entity.IncidentCategory;
import com.gehu.safety.entity.IncidentStatus;
import com.gehu.safety.entity.PriorityLevel;

import java.util.List;

public interface IncidentService {

    /** Preview only - runs the AI analysis, persists nothing. */
    IncidentAnalysisResponse analyze(CreateIncidentAnalyzeRequest request);

    IncidentResponse createIncident(Long userId, CreateIncidentRequest request);

    List<IncidentResponse> getMyIncidents(Long userId);

    // ---- Admin/Moderator ----
    PageResponse<IncidentAdminResponse> getAllIncidents(IncidentCategory category, PriorityLevel priority,
                                                         IncidentStatus status, int page, int size);

    IncidentAdminResponse reviewIncident(Long reviewerUserId, Long incidentId, ReviewIncidentRequest request);
}
