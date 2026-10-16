package com.gehu.safety.service.impl;

import com.gehu.safety.ai.AiTextAnalysisService;
import com.gehu.safety.ai.dto.IncidentAnalysis;
import com.gehu.safety.dto.request.CreateIncidentAnalyzeRequest;
import com.gehu.safety.dto.request.CreateIncidentRequest;
import com.gehu.safety.dto.request.ReviewIncidentRequest;
import com.gehu.safety.dto.response.*;
import com.gehu.safety.entity.*;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.IncidentReportRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.AuditService;
import com.gehu.safety.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentReportRepository incidentReportRepository;
    private final UserRepository userRepository;
    private final AiTextAnalysisService aiTextAnalysisService;
    private final AuditService auditService;

    @Override
    public IncidentAnalysisResponse analyze(CreateIncidentAnalyzeRequest request) {
        IncidentAnalysis analysis = aiTextAnalysisService.analyzeIncident(request.getDescription());

        return IncidentAnalysisResponse.builder()
                .suggestedCategory(analysis.getClassification().getCategory())
                .confidence(analysis.getClassification().getConfidence())
                .categoryReasoning(analysis.getClassification().getReasoning())
                .suggestedPriority(analysis.getPriority().getPriority())
                .priorityReasoning(analysis.getPriority().getReasoning())
                .summary(analysis.getSummary())
                .build();
    }

    @Override
    @Transactional
    public IncidentResponse createIncident(Long userId, CreateIncidentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        IncidentAnalysis analysis = aiTextAnalysisService.analyzeIncident(request.getDescription());

        IncidentReport report = IncidentReport.builder()
                .user(user)
                .description(request.getDescription())
                .category(request.getCategory()) // reporter's own choice, defaults to AI suggestion client-side
                .priority(analysis.getPriority().getPriority()) // AI-suggested initially; admin can override
                .aiSuggestedCategory(analysis.getClassification().getCategory())
                .aiSuggestedPriority(analysis.getPriority().getPriority())
                .aiConfidence(analysis.getClassification().getConfidence())
                .aiReasoning(analysis.getClassification().getReasoning() + "; " + analysis.getPriority().getReasoning())
                .aiSummary(analysis.getSummary())
                .lat(request.getLat())
                .lng(request.getLng())
                .areaLabel(request.getAreaLabel())
                .visibilityType(request.getVisibilityType())
                .status(IncidentStatus.SUBMITTED)
                .build();

        report = incidentReportRepository.save(report);

        auditService.record(userId, "INCIDENT_CREATED", "INCIDENT_REPORT", report.getId(),
                "AI suggested " + analysis.getClassification().getCategory() + "/" + analysis.getPriority().getPriority());

        return toResponse(report);
    }

    @Override
    public List<IncidentResponse> getMyIncidents(Long userId) {
        return incidentReportRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<IncidentAdminResponse> getAllIncidents(IncidentCategory category, PriorityLevel priority,
                                                                IncidentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<IncidentReport> results = incidentReportRepository.search(category, priority, status, pageable);
        return PageResponse.from(results.map(this::toAdminResponse));
    }

    @Override
    @Transactional
    public IncidentAdminResponse reviewIncident(Long reviewerUserId, Long incidentId, ReviewIncidentRequest request) {
        IncidentReport report = incidentReportRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident report not found"));

        if (request.getCategory() != null && request.getCategory() != report.getCategory()) {
            auditService.record(reviewerUserId, "INCIDENT_CATEGORY_OVERRIDDEN", "INCIDENT_REPORT", report.getId(),
                    report.getCategory() + " -> " + request.getCategory());
            report.setCategory(request.getCategory());
        }
        if (request.getPriority() != null && request.getPriority() != report.getPriority()) {
            auditService.record(reviewerUserId, "INCIDENT_PRIORITY_OVERRIDDEN", "INCIDENT_REPORT", report.getId(),
                    report.getPriority() + " -> " + request.getPriority());
            report.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            report.setStatus(request.getStatus());
        }
        if (request.getAssignedToUserId() != null) {
            User assignee = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            report.setAssignedTo(assignee);
            auditService.record(reviewerUserId, "INCIDENT_ASSIGNED", "INCIDENT_REPORT", report.getId(),
                    "Assigned to " + assignee.getName());
            // Assigning naturally implies the ASSIGNED stage unless the caller explicitly set a different status.
            if (request.getStatus() == null) {
                report.setStatus(IncidentStatus.ASSIGNED);
            }
        }

        User reviewer = userRepository.findById(reviewerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        report.setReviewedBy(reviewer);
        report.setReviewedAt(LocalDateTime.now());

        report = incidentReportRepository.save(report);
        return toAdminResponse(report);
    }

    private IncidentResponse toResponse(IncidentReport r) {
        return IncidentResponse.builder()
                .id(r.getId())
                .description(r.getDescription())
                .category(r.getCategory())
                .priority(r.getPriority())
                .aiSuggestedCategory(r.getAiSuggestedCategory())
                .aiSuggestedPriority(r.getAiSuggestedPriority())
                .aiConfidence(r.getAiConfidence())
                .areaLabel(r.getAreaLabel())
                .visibilityType(r.getVisibilityType())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private IncidentAdminResponse toAdminResponse(IncidentReport r) {
        String reporterName = (r.getVisibilityType() == IncidentVisibility.NAMED_ADMIN_ONLY)
                ? r.getUser().getName()
                : null; // anonymity boundary respected even for admins on ANON_PUBLIC/FULL_ANON reports

        return IncidentAdminResponse.builder()
                .id(r.getId())
                .reporterName(reporterName)
                .description(r.getDescription())
                .aiSummary(r.getAiSummary())
                .category(r.getCategory())
                .priority(r.getPriority())
                .aiSuggestedCategory(r.getAiSuggestedCategory())
                .aiSuggestedPriority(r.getAiSuggestedPriority())
                .aiConfidence(r.getAiConfidence())
                .aiReasoning(r.getAiReasoning())
                .assignedToName(r.getAssignedTo() != null ? r.getAssignedTo().getName() : null)
                .lat(r.getLat())
                .lng(r.getLng())
                .areaLabel(r.getAreaLabel())
                .visibilityType(r.getVisibilityType())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
