package com.gehu.safety.controller;

import com.gehu.safety.dto.request.JourneyPingRequest;
import com.gehu.safety.dto.request.StartJourneyRequest;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.JourneyResponse;
import com.gehu.safety.dto.response.JourneyShareResponse;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.JourneyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journeys")
public class JourneyController {

    private final JourneyService journeyService;


    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<JourneyResponse>> startJourney(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StartJourneyRequest request) {

        JourneyResponse response = journeyService.startJourney(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Journey started", response));
    }

    @PostMapping("/{journeyId}/ping")
    public ApiResponse<JourneyResponse> ping(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long journeyId,
            @Valid @RequestBody JourneyPingRequest request) {

        return ApiResponse.success(journeyService.ping(principal.getId(), journeyId, request));
    }

    @PostMapping("/{journeyId}/checkin")
    public ApiResponse<JourneyResponse> checkIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long journeyId) {

        return ApiResponse.success("Marked as reached safely", journeyService.checkIn(principal.getId(), journeyId));
    }

    @PostMapping("/{journeyId}/cancel")
    public ApiResponse<JourneyResponse> cancel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long journeyId) {

        return ApiResponse.success("Journey cancelled", journeyService.cancelJourney(principal.getId(), journeyId));
    }

    @GetMapping("/active")
    public ApiResponse<JourneyResponse> getActive(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(journeyService.getActiveJourney(principal.getId()));
    }

    @GetMapping("/history")
    public ApiResponse<List<JourneyResponse>> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(journeyService.getHistory(principal.getId()));
    }

    /**
     * Public endpoint - no login. The unguessable token IS the credential.
     * See SecurityConfig (/api/journeys/track/** -> permitAll) and
     * JourneyShareResponse's javadoc for exactly what this exposes.
     */
    @GetMapping("/track/{token}")
    public ApiResponse<JourneyShareResponse> track(@PathVariable String token) {
        return ApiResponse.success(journeyService.getShareView(token));
    }
}
