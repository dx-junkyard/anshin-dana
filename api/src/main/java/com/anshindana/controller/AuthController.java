package com.anshindana.controller;

import com.anshindana.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/line")
    public ResponseEntity<AuthService.AuthResponse> exchange(@Valid @RequestBody LineAuthRequest request) {
        return ResponseEntity.ok(authService.exchangeLineIdToken(request.idToken(), request.displayName(), request.pictureUrl()));
    }

    public record LineAuthRequest(@NotBlank String idToken, String displayName, String pictureUrl) {
    }
}
