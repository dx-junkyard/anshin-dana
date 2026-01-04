package com.anshindana.controller;

import com.anshindana.service.StockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consume")
public class ConsumeController {

    private final StockService stockService;

    public ConsumeController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<StockService.ConsumptionResult> consume(@Valid @RequestBody ConsumeRequest request) {
        return ResponseEntity.ok(stockService.consume(request.stockItemId(), request.quantity()));
    }

    public record ConsumeRequest(@NotNull Long stockItemId, @Positive int quantity) {
    }
}
