package com.studyenglish.backend.auth;

import com.studyenglish.backend.config.AppProperties;
import com.studyenglish.backend.users.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {
    private final JwtEncoder encoder;
    private final AppProperties props;

    public TokenService(JwtEncoder encoder, AppProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public String issue(User u) {
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(u.getId().toString())
                .claim("role", u.getRole().name())
                .claim("nickname", u.getNickname())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenTtlSeconds()))
                .build();
        var header = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long accessTokenTtlSeconds() {
        return props.jwt().accessTokenTtlMinutes() * 60;
    }
}
