package com.studyenglish.backend.dtos.requests.update;

import jakarta.validation.constraints.*;

public record ProfileUpdateDto(
        @Pattern(regexp = "^(?!\\s*$).+", message = "Имя не может быть пустым")
        @Size(max = 100, message = "Имя не должно быть длиннее 100 символов")
        String name,
        @Size(min = 3, max = 50, message = "Никнейм должен быть от 3 до 50 символов")
        @Pattern(regexp = "^[A-Za-z0-9_]*$",
                message = "Никнейм может содержать только латинские буквы, цифры и _")
        String nickname
) {
}
