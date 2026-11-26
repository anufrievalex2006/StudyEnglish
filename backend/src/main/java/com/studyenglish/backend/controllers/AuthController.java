package com.studyenglish.backend.controllers;

import com.studyenglish.backend.dtos.responses.AuthTokens;
import com.studyenglish.backend.utilities.RefreshCookieFactory;
import com.studyenglish.backend.dtos.responses.AuthResponse;
import com.studyenglish.backend.dtos.requests.auth.LoginDto;
import com.studyenglish.backend.dtos.requests.auth.RegisterDto;
import com.studyenglish.backend.dtos.responses.UserResponse;
import com.studyenglish.backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final RefreshCookieFactory factory;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterDto req) {
        return UserResponse.from(service.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto req) {
        return tokensResponse(service.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = RefreshCookieFactory.NAME, required = false) String rtoken) {
        return tokensResponse(service.refresh(rtoken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = RefreshCookieFactory.NAME, required = false) String rtoken) {
        service.logout(rtoken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, factory.clear().toString())
                .build();
    }

    private ResponseEntity<AuthResponse> tokensResponse(AuthTokens tokens) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.SET_COOKIE, factory.create(tokens).toString())
                .body(new AuthResponse(tokens.getAccessToken(), tokens.getExpiresIn()));
    }
}
