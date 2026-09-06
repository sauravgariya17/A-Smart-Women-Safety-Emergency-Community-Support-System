package com.gehu.safety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private List<String> errors; // field-level validation messages, if any

    public ApiErrorResponse(String message, int status, List<String> errors) {
        this.success = false;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}
