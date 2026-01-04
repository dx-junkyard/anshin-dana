package com.anshindana.controller;

import com.anshindana.service.StockService;
import com.anshindana.service.dto.StockItemView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<StockItemView> register(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody RegisterStockRequest request) {
        StockItemView item = stockService.registerStock(
                parseUserId(jwt),
                request.barcode(),
                request.name(),
                request.brand(),
                request.category(),
                request.quantity(),
                request.unit(),
                request.expiresOn(),
                request.purchasedOn()
        );
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<StockItemView>> list(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "expiresSoon") String sort) {
        return ResponseEntity.ok(stockService.listStocks(parseUserId(jwt), sort));
    }

    private Long parseUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    public record RegisterStockRequest(
            String barcode,
            @NotBlank String name,
            String brand,
            @NotBlank String category,
            @Positive int quantity,
            @NotBlank String unit,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiresOn,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchasedOn
    ) {
    }
}
