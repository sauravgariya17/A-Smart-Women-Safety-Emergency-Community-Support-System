package com.gehu.safety.controller;

import com.gehu.safety.dto.request.TriggerSosRequest;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.SosResponse;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.SosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosController {

    private final SosService sosService;

    /** Step 1: creates PENDING_CONFIRMATION. No one is notified yet. */
    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse<SosResponse>> trigger(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TriggerSosRequest request) {

        SosResponse response = sosService.trigger(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("SOS pending confirmation", response));
    }

    /** Step 2: the actual "confirm you meant this" action. Fires notifications. */
    @PostMapping("/{sosId}/confirm")
    public ApiResponse<SosResponse> confirm(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long sosId) {

        return ApiResponse.success("Emergency confirmed. Trusted contacts are being notified.",
                sosService.confirm(principal.getId(), sosId));
    }

    /** Available at any pre-resolved stage: "cancel accidental trigger" or "I'm safe now". */
    @PostMapping("/{sosId}/cancel")
    public ApiResponse<SosResponse> cancel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long sosId) {

        return ApiResponse.success("SOS cancelled", sosService.cancel(principal.getId(), sosId));
    }

    @GetMapping("/active")
    public ApiResponse<SosResponse> getActive(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(sosService.getMyActive(principal.getId()));
    }

    @GetMapping("/history")
    public ApiResponse<List<SosResponse>> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(sosService.getMyHistory(principal.getId()));
    }
}
