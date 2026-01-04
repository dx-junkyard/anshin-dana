package com.anshindana.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anshindana.config.JwtProperties;
import com.anshindana.domain.User;
import com.anshindana.domain.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    private final SecretKey signingKey;
    private final JwtProperties jwtProperties;
    private final LineIdTokenVerifier lineIdTokenVerifier;
    private final UserRepository userRepository;

    public AuthService(JwtProperties jwtProperties,
                       LineIdTokenVerifier lineIdTokenVerifier,
                       UserRepository userRepository) {
        this.jwtProperties = jwtProperties;
        this.lineIdTokenVerifier = lineIdTokenVerifier;
        this.userRepository = userRepository;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public AuthResponse exchangeLineIdToken(String idToken, String displayNameOverride, String pictureUrlOverride) {
        LineIdTokenVerifier.LineProfile lineProfile = lineIdTokenVerifier.verify(idToken);

        String displayName = firstNonBlank(displayNameOverride, lineProfile.displayName());
        String pictureUrl = firstNonBlank(pictureUrlOverride, lineProfile.pictureUrl());

        User user = userRepository.findByLineSub(lineProfile.sub())
                .map(existing -> updateUser(existing, displayName, pictureUrl))
                .orElseGet(() -> new User(lineProfile.sub(), displayName, pictureUrl));
        userRepository.save(user);

        String appToken = generateAppToken(user, lineProfile.sub());
        return new AuthResponse(appToken, new UserResponse(user.getId(), user.getLineSub(), user.getDisplayName(), user.getPictureUrl()));
    }

    private User updateUser(User user, String displayName, String pictureUrl) {
        if (displayName != null) {
            user.setDisplayName(displayName);
        }
        if (pictureUrl != null) {
            user.setPictureUrl(pictureUrl);
        }
        return user;
    }

    private String generateAppToken(User user, String lineSub) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("line_sub", lineSub);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuer(jwtProperties.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.expiresMinutes(), ChronoUnit.MINUTES)))
                .claims(claims)
                .signWith(signingKey)
                .compact();
    }

    private String firstNonBlank(String value, String fallback) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return null;
    }

    public record AuthResponse(String token, UserResponse user) {
    }

    public record UserResponse(Long id, String lineSub, String displayName, String pictureUrl) {
    }
}
