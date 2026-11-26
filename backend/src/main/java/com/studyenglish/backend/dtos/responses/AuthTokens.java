package com.studyenglish.backend.dtos.responses;

import java.time.Duration;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokens {
    private String accessToken;
    private long expiresIn;
    private String refreshToken;
    private boolean persistent;
    private Duration refreshTtl;
}
