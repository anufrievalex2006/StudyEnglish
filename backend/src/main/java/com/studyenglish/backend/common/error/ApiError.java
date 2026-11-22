package com.studyenglish.backend.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        ErrorCode code,
        String message,
        String path,
        Instant timestamp,
        List<FieldErrorDetail> fieldErrors
) {
    public static ApiError of(int status, ErrorCode code, String message, String path) {
        return new ApiError(status, code, message, path, Instant.now(), null);
    }
    public static ApiError of(int status, ErrorCode code, String message, String path, List<FieldErrorDetail> fieldErrors) {
        return new ApiError(status, code, message, path, Instant.now(), fieldErrors);
    }
}
