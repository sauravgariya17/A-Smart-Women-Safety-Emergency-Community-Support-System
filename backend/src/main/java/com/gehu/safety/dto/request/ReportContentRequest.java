package com.gehu.safety.dto.request;

import com.gehu.safety.entity.TargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportContentRequest {

    @NotNull(message = "targetType is required (POST or COMMENT)")
    private TargetType targetType;

    @NotNull(message = "targetId is required")
    private Long targetId;

    @Size(max = 500, message = "Reason must be under 500 characters")
    private String reason;
}
