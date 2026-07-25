package com.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for REST endpoints.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

    /**
     * HTTP status code of the response.
     */
    @NotNull(message = "Status code is required")
    private int statusCode;

    /**
     * Human-readable message describing the outcome.
     */
    private String message;

    /**
     * The actual data payload (if any).
     */
    private T data;

    /**
     * Error details in case of a failure response.
     */
    private ErrorResponse error;

    // Generic error response structure
    @Data
    public static class ErrorResponse {
        private String code;
        private String message;
        private java.util.List<String> details;
    }
}
