package com.gehu.safety.controller;

import com.gehu.safety.dto.request.CreateIncidentAnalyzeRequest;
import com.gehu.safety.dto.request.CreateIncidentRequest;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.IncidentAnalysisResponse;
import com.gehu.safety.dto.response.IncidentResponse;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;


    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }
    /** Preview-only - runs AI classification/priority/summary WITHOUT saving anything. */
    @PostMapping("/analyze")
    public ApiResponse<IncidentAnalysisResponse> analyze(@Valid @RequestBody CreateIncidentAnalyzeRequest request) {
        return ApiResponse.success(incidentService.analyze(request));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IncidentResponse>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateIncidentRequest request) {

        IncidentResponse response = incidentService.createIncident(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Incident report submitted", response));
    }

    @GetMapping("/mine")
    public ApiResponse<List<IncidentResponse>> getMine(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(incidentService.getMyIncidents(principal.getId()));
    }
}
