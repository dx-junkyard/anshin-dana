package com.anshindana.controller;

import com.anshindana.service.StockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scan")
public class ScanController {

    private final StockService stockService;

    public ScanController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> scan(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ScanRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("productCandidate", stockService.findProductByBarcode(request.barcode()).orElse(null));
        response.put("lastUsedExpiryTemplates", stockService.lastUsedExpiryTemplates(parseUserId(jwt), request.barcode()));
        return ResponseEntity.ok(response);
    }

    private Long parseUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    public record ScanRequest(@NotBlank String barcode) {
    }
}
