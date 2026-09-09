package com.gehu.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Comment body is required")
    @Size(min = 1, max = 2000, message = "Comment must be under 2000 characters")
    private String body;
}
