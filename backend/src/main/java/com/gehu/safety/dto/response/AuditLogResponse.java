package com.gehu.safety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private String actorName;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String reason;
    private LocalDateTime timestamp;
}
