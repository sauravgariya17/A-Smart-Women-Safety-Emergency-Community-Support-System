package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Uniform success-response envelope so the frontend always parses the
 * same shape: { success, message, data }.
 */
@Data
@NoArgsConstructor

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Request successful", data);
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
