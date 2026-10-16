package com.gehu.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Preview-only: does not persist anything. Lets the reporter see the AI's
 * suggestion before deciding what to actually submit. */
@Data
public class CreateIncidentAnalyzeRequest {

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 3000, message = "Description must be between 10 and 3000 characters")
    private String description;
}
