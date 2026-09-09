package com.gehu.safety.dto.request;

import com.gehu.safety.entity.ModerationQueueStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModerationReviewRequest {

    @NotNull(message = "decision is required (APPROVED or REMOVED)")
    private ModerationQueueStatus decision;
}
