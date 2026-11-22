package com.studyenglish.backend.auth;

import java.time.Duration;

public record AuthTokens(
        String accessToken,
        long expiresIn,
        String refreshToken,
        boolean persistent,
        Duration refreshTtl
) {
}
