package com.anshindana.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class LineIdTokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(LineIdTokenVerifier.class);

    private final JwtDecoder lineJwtDecoder;

    public LineIdTokenVerifier(@Qualifier("lineJwtDecoder") JwtDecoder lineJwtDecoder) {
        this.lineJwtDecoder = lineJwtDecoder;
    }

    public LineProfile verify(String idToken) {
        try {
            Jwt jwt = lineJwtDecoder.decode(idToken);
            String sub = jwt.getSubject();
            String displayName = jwt.getClaimAsString("name");
            String pictureUrl = jwt.getClaimAsString("picture");
            return new LineProfile(sub, displayName, pictureUrl);
        } catch (JwtException e) {
            log.warn("Failed to verify LINE ID token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid LINE token");
        }
    }

    public record LineProfile(String sub, String displayName, String pictureUrl) {
    }
}
