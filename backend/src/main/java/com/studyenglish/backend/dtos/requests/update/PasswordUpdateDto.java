package com.studyenglish.backend.dtos.requests.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordUpdateDto(
        @NotBlank(message = "Введите текущий пароль")
        String currentPassword,
        @NotBlank(message = "Введите новый пароль")
        @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов")
        String newPassword
) {
}
