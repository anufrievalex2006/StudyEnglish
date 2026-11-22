package com.studyenglish.backend.auth.dto;

import jakarta.validation.constraints.*;

public record RegisterDto(
        @NotBlank(message = "Введите имя")
        @Size(max = 100, message = "Имя не должно быть длиннее 100 символов")
        String name,
        @NotBlank(message = "Введите никнейм")
        @Size(min = 3, max = 50, message = "Никнейм должен быть от 3 до 50 символов")
        @Pattern(regexp = "^[A-Za-z0-9_]*$",
                message = "Никнейм может содержать только латинские буквы, цифры и _")
        String nickname,
        @NotBlank(message = "Введите email")
        @Email(message = "Некорректный email")
        @Size(max = 255, message = "Email не должен быть длиннее 255 символов")
        String email,
        @NotBlank(message = "Введите пароль")
        @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов")
        String password
) {
}
