package com.studyenglish.backend.dtos.requests.update;

import com.studyenglish.backend.models.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateDto(
        @NotNull(message = "Укажите роль")
        UserRole role
) {
}
