package com.gehu.safety.controller;

import com.gehu.safety.dto.request.ModerationReviewRequest;
import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.ModerationQueueItemResponse;
import com.gehu.safety.dto.response.PageResponse;
import com.gehu.safety.security.UserPrincipal;
import com.gehu.safety.service.ModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Restricted to MODERATOR/ADMIN both here (@PreAuthorize, defense in depth)
 * and in SecurityConfig's route rules (/api/moderation/** -> hasAnyRole).
 */
@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
public class ModerationController {

    private final ModerationService moderationService;

    @GetMapping("/queue")
    public ApiResponse<PageResponse<ModerationQueueItemResponse>> getQueue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ApiResponse.success(moderationService.getPendingQueue(page, size));
    }

    @PostMapping("/queue/{queueId}/review")
    public ApiResponse<Void> reviewItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long queueId,
            @Valid @RequestBody ModerationReviewRequest request) {

        moderationService.reviewItem(principal.getId(), queueId, request);
        return ApiResponse.success("Review recorded", null);
    }
}
