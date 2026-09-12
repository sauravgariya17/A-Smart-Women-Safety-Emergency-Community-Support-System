package com.gehu.safety.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class StartJourneyRequest {

    @NotNull(message = "Start latitude is required")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double startLat;

    @NotNull(message = "Start longitude is required")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double startLng;

    @NotNull(message = "Destination latitude is required")
    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double destLat;

    @NotNull(message = "Destination longitude is required")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double destLng;

    @NotNull(message = "Expected duration in minutes is required")
    @Min(value = 1, message = "Expected duration must be at least 1 minute")
    @Max(value = 180, message = "Expected duration must be under 3 hours for a campus journey")
    private Integer expectedDurationMinutes;

    /** IDs of the caller's own trusted contacts to notify for this journey. Empty allowed but discouraged. */
    private List<Long> trustedContactIds;
}
