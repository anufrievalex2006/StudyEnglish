package com.studyenglish.backend.auth.dto;

public record AuthResponse(String accessToken, long expiresIn) {
}
