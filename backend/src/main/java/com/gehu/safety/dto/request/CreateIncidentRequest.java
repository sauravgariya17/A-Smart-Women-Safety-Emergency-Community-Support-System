package com.gehu.safety.dto.request;

import com.gehu.safety.entity.IncidentCategory;
import com.gehu.safety.entity.IncidentVisibility;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateIncidentRequest {

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 3000, message = "Description must be between 10 and 3000 characters")
    private String description;

    /** Reporter's chosen category - defaults to the AI suggestion in the UI, but the reporter has final say on their own report's label. */
    @NotNull(message = "Category is required")
    private IncidentCategory category;

    @NotNull(message = "Visibility is required")
    private IncidentVisibility visibilityType;

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double lat;

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double lng;

    @Size(max = 120)
    private String areaLabel;
}
