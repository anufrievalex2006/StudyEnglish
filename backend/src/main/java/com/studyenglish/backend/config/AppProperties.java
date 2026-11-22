package com.studyenglish.backend.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @Valid @NotNull Admin admin,
        @Valid @NotNull Jwt jwt
) {
    public record Admin(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password,
            @NotBlank String nickname
    ) {
    }

    public record Jwt(
            @NotBlank String secretBase64,
            @Positive long accessTokenTtlMinutes
    ) {
    }
}
