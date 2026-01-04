package com.anshindana.controller;

import com.anshindana.domain.ProductCandidate;
import com.anshindana.service.StockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/scan")
public class ScanController {

    private final StockService stockService;

    public ScanController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> scan(@Valid @RequestBody ScanRequest request) {
        ProductCandidate candidate = stockService.findProductByBarcode(request.barcode());
        return ResponseEntity.ok(Map.of(
                "productCandidate", candidate,
                "lastUsedExpiryTemplates", stockService.lastUsedExpiryTemplates(1L, request.barcode())
        ));
    }

    public record ScanRequest(@NotBlank String barcode) {
    }
}
