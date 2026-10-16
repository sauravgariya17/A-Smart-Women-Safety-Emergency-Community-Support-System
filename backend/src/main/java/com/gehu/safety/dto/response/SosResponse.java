package com.gehu.safety.dto.response;

import com.gehu.safety.entity.SosCategory;
import com.gehu.safety.entity.SosStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SosResponse {
    private Long id;
    private SosCategory category;
    private Double lat;
    private Double lng;
    private SosStatus status;
    private LocalDateTime confirmationExpiresAt;
    private LocalDateTime triggeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private String notes;
}
