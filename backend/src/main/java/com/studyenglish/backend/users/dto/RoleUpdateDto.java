package com.studyenglish.backend.users.dto;

import com.studyenglish.backend.users.UserRole;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateDto(
        @NotNull(message = "Укажите роль")
        UserRole role
) {
}
