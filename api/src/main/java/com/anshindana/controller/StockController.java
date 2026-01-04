package com.anshindana.controller;

import com.anshindana.domain.StockItem;
import com.anshindana.service.StockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<StockItem> register(@Valid @RequestBody RegisterStockRequest request) {
        StockItem item = stockService.registerStock(
                request.name(),
                request.category(),
                request.quantity(),
                request.unit(),
                request.expiresOn()
        );
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<StockItem>> list(@RequestParam(defaultValue = "expiresSoon") String sort) {
        return ResponseEntity.ok(stockService.listStocks());
    }

    public record RegisterStockRequest(
            String barcode,
            @NotBlank String name,
            @NotBlank String category,
            @Positive int quantity,
            @NotBlank String unit,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiresOn
    ) {
    }
}
