package com.gehu.safety.dto.response;

import com.gehu.safety.entity.ModerationQueueStatus;
import com.gehu.safety.entity.ReportSource;
import com.gehu.safety.entity.TargetType;
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

    public ModerationQueueItemResponse(Long queueId, TargetType targetType, Long targetId, String reason, ReportSource flaggedBy, ModerationQueueStatus status, String contentPreview, LocalDateTime createdAt) {
        this.queueId = queueId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.flaggedBy = flaggedBy;
        this.status = status;
        this.contentPreview = contentPreview;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ModerationQueueItemResponse response = new ModerationQueueItemResponse();

        public Builder queueId(Long value) { response.queueId = value; return this; }
        public Builder targetType(TargetType value) { response.targetType = value; return this; }
        public Builder targetId(Long value) { response.targetId = value; return this; }
        public Builder reason(String value) { response.reason = value; return this; }
        public Builder flaggedBy(ReportSource value) { response.flaggedBy = value; return this; }
        public Builder status(ModerationQueueStatus value) { response.status = value; return this; }
        public Builder contentPreview(String value) { response.contentPreview = value; return this; }
        public Builder contentAuthorHandle(String value) { response.contentAuthorHandle = value; return this; }
        public Builder createdAt(LocalDateTime value) { response.createdAt = value; return this; }
        public ModerationQueueItemResponse build() { return response; }
    }
}
