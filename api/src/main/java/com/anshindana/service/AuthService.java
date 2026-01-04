package com.anshindana.service;

import com.anshindana.domain.UserProfile;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class AuthService {

    private final SecretKey key;

    public AuthService(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public AuthResponse exchangeLineIdToken(String idToken) {
        // NOTE: Mock implementation. In production, verify the LINE ID token via JWKS.
        UserProfile profile = new UserProfile(1L, "Mock User", "https://example.com/avatar.png");
        String token = Jwts.builder()
                .subject(profile.id().toString())
                .issuer("anshin-dana")
                .issuedAt(java.util.Date.from(Instant.now()))
                .expiration(java.util.Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .claims(Map.of("displayName", profile.displayName()))
                .signWith(key)
                .compact();
        return new AuthResponse(token, profile);
    }

    public record AuthResponse(String token, UserProfile user) {
    }
}
