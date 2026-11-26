package com.studyenglish.backend.services;

import com.studyenglish.backend.dtos.responses.AuthTokens;
import com.studyenglish.backend.dtos.requests.auth.LoginDto;
import com.studyenglish.backend.dtos.requests.auth.RegisterDto;
import com.studyenglish.backend.exceptions.ApiException;
import com.studyenglish.backend.models.api.RefreshToken;
import com.studyenglish.backend.models.api.User;
import com.studyenglish.backend.repos.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Duration REMEMBER_ME_TTL = Duration.ofDays(30);
    private static final Duration SESSION_TTL = Duration.ofDays(1);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserService service;
    private final RefreshTokenRepo repo;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    public User register(RegisterDto req) {
        return service.createStudent(req.getName(), req.getNickname(), req.getEmail(), req.getPassword());
    }

    @Transactional
    public AuthTokens login(LoginDto req) {
        User u = service.findByEmail(req.getEmail().trim())
                .orElseThrow(ApiException::invalidCredentials);

        if (!encoder.matches(req.getPassword(), u.getPasswordHash()))
            throw ApiException.invalidCredentials();
        return issueTokens(u, req.isRememberMe());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public AuthTokens refresh(String token) {
        if (token == null || token.isBlank())
            throw ApiException.invalidToken();

        RefreshToken rt = repo.findByTokenHash(sha256Hex(token))
                .orElseThrow(ApiException::invalidToken);
        if (rt.isRevoked()) {
            repo.revokeAllActiveByUserId(rt.getUser().getId());
            throw ApiException.invalidToken();
        }
        if (rt.getExpiresAt().isBefore(Instant.now())) {
            repo.delete(rt);
            throw ApiException.invalidToken();
        }

        rt.revoke();
        return issueTokens(rt.getUser(), rt.isPersistent());
    }

    @Transactional
    public void logout(String token) {
        if (token == null || token.isBlank())
            return;

        repo.findByTokenHash(sha256Hex(token)).ifPresent(RefreshToken::revoke);
    }

    private AuthTokens issueTokens(User u, boolean persistent) {
        String rtoken = generateRawToken();
        Duration ttl = persistent ? REMEMBER_ME_TTL : SESSION_TTL;

        repo.save(RefreshToken.builder()
                .tokenHash(sha256Hex(rtoken))
                .user(u)
                .expiresAt(Instant.now().plus(ttl))
                .persistent(persistent)
                .build());
        return new AuthTokens(tokenService.issue(u), tokenService.accessTokenTtlSeconds(), rtoken, persistent, ttl);
    }

    private static String generateRawToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 недоступен", e);
        }
    }
}
