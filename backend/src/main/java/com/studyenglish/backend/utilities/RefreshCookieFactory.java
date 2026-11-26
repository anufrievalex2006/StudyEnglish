package com.studyenglish.backend.utilities;

import com.studyenglish.backend.dtos.responses.AuthTokens;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshCookieFactory {
    public static final String NAME = "refresh_token";
    private static final String PATH = "/api/auth";
    private final boolean secure;

    public RefreshCookieFactory(@Value("${app.cookie.secure:false}") boolean secure) {
        this.secure = secure;
    }

    public ResponseCookie create(AuthTokens tokens) {
        ResponseCookie.ResponseCookieBuilder builder = base(tokens.getRefreshToken());
        if (tokens.isPersistent())
            builder.maxAge(tokens.getRefreshTtl());
        return builder.build();
    }

    public ResponseCookie clear() {
        return base("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path(PATH);
    }
}
