package com.studyenglish.backend.exceptions;

import com.studyenglish.backend.models.enums.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String VALIDATION_MSG = "Проверьте введенные данные";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> onApiException(ApiException e, WebRequest req) {
        ApiError body = ApiError.of(e.getStatus().value(), e.getCode(), e.getMessage(), path(req));
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> onConstraintViolation(ConstraintViolationException e, WebRequest req) {
        List<FieldErrorDetail> fieldErrors = e.getConstraintViolations().stream()
                .map(x -> new FieldErrorDetail(lastNode(x.getPropertyPath().toString()), x.getMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError(fieldErrors, req));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> onAccessDenied(AccessDeniedException e, WebRequest req) {
        ApiError body = ApiError.of(HttpStatus.FORBIDDEN.value(), ErrorCode.ACCESS_DENIED, "Недостаточно прав для выполнения этой операции", path(req));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> onUnexpected(Exception e, WebRequest req) {
        log.error("Необработанная ошибка при запросе {}", path(req), e);
        ApiError body = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.INTERNAL_ERROR, "Внутренняя ошибка сервера, пожалуйста повторите позже", path(req));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest req) {
        List<FieldErrorDetail> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> new FieldErrorDetail(x.getField(), x.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError(fieldErrors, req));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatusCode code, WebRequest req) {
        int status = code.value();
        ErrorCode c = switch (status) {
            case 400 -> ErrorCode.VALIDATION_ERROR;
            case 404 -> ErrorCode.NOT_FOUND;
            case 500 -> ErrorCode.INTERNAL_ERROR;
            default -> ErrorCode.REQUEST_ERROR;
        };
        String msg = switch (status) {
            case 400 -> "Некорректный запрос";
            case 404 -> "Ресурс не найден";
            case 405 -> "Метод запроса не поддерживается";
            case 415 -> "Неподдерживаемый формат данных";
            case 500 -> "Внутренняя ошибка сервера, пожалуйста повторите позже";
            default -> "Ошибка обработки запроса";
        };
        ApiError error = ApiError.of(status, c, msg, path(req));
        return ResponseEntity.status(code).headers(headers).body(error);
    }

    private static ApiError validationError(List<FieldErrorDetail> fieldErrors, WebRequest req) {
        return ApiError.of(HttpStatus.BAD_REQUEST.value(), ErrorCode.VALIDATION_ERROR, VALIDATION_MSG, path(req), fieldErrors);
    }

    private static String path(WebRequest req) {
        if (req instanceof ServletWebRequest sreq)
            return sreq.getRequest().getRequestURI();

        return "";
    }

    private static String lastNode(String propertyPath) {
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }
}
