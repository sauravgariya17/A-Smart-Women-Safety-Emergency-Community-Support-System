package com.gehu.safety.dto.response;

import com.gehu.safety.entity.ModerationQueueStatus;
import com.gehu.safety.entity.ReportSource;
import com.gehu.safety.entity.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Deliberately has NO reporterUserId field - a moderator sees what was
 * flagged and why, never who flagged it, to avoid retaliation and keep
 * review focused on content, not the reporter.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationQueueItemResponse {
    private Long queueId;
    private TargetType targetType;
    private Long targetId;
    private String reason;
    private ReportSource flaggedBy;
    private ModerationQueueStatus status;

    // A content preview so the moderator doesn't have to open another tab
    private String contentPreview;
    private String contentAuthorHandle; // anonymous handle of whoever wrote the flagged content

    private LocalDateTime createdAt;
}
