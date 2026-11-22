package com.studyenglish.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @NotBlank(message = "Введите email")
        String email,
        @NotBlank(message = "Введите пароль")
        String password,
        boolean rememberMe
) {
}
