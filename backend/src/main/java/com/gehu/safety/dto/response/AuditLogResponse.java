package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor

public class AuditLogResponse {
    private Long id;
    private String actorName;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String reason;
    private LocalDateTime timestamp;

    public AuditLogResponse(Long id, String actorName, String actionType, String targetType, Long targetId, String reason, LocalDateTime timestamp) {
        this.id = id;
        this.actorName = actorName;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.timestamp = timestamp;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AuditLogResponse value = new AuditLogResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder actorName(String value) { this.value.actorName = value; return this; }
        public Builder actionType(String value) { this.value.actionType = value; return this; }
        public Builder targetType(String value) { this.value.targetType = value; return this; }
        public Builder targetId(Long value) { this.value.targetId = value; return this; }
        public Builder reason(String value) { this.value.reason = value; return this; }
        public Builder timestamp(LocalDateTime value) { this.value.timestamp = value; return this; }
        public AuditLogResponse build() { return value; }
    }
}
