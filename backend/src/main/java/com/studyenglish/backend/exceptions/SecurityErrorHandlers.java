package com.studyenglish.backend.exceptions;

import com.studyenglish.backend.models.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SecurityErrorHandlers {
    private final ObjectMapper mapper;

    public AuthenticationEntryPoint unauthorized() {
        return (HttpServletRequest req, HttpServletResponse res, AuthenticationException e) -> write(res, req, HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN, "Сессия недействительна, войдите еще раз");
    }

    public AccessDeniedHandler forbidden() {
        return (HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) -> write(res, req, HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED, "Недостаточно прав для выполнения этой операции");
    }

    private void write(HttpServletResponse response, HttpServletRequest request,
                       HttpStatus status, ErrorCode code, String message) throws IOException {
        ApiError body = ApiError.of(status.value(), code, message, request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
