package com.gehu.safety.dto.request;

import com.gehu.safety.entity.SosStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResolveSosRequest {

    @NotNull(message = "outcome is required (RESOLVED or FALSE_ALARM)")
    private SosStatus outcome;

    @Size(max = 1000)
    private String notes;
}
