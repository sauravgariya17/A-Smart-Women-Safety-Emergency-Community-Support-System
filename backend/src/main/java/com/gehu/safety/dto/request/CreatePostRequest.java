package com.gehu.safety.dto.request;

import com.gehu.safety.entity.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Post body is required")
    @Size(min = 10, max = 5000, message = "Post body must be between 10 and 5000 characters")
    private String body;

    @NotNull(message = "Category is required")
    private PostCategory category;

    /**
     * Optional, coarse area only (e.g. "Near Main Gate", "Library Block").
     * Never accept latitude/longitude here - keep exact location reporting
     * inside the separate Incident Reporting module which has stricter
     * access control.
     */
    @Size(max = 120, message = "Area label must be under 120 characters")
    private String areaLabel;
}
