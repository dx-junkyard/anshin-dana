package com.anshindana.controller;

import com.anshindana.domain.ConsumptionReason;
import com.anshindana.service.StockService;
import com.anshindana.service.dto.ConsumptionResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/consume")
public class ConsumeController {

    private final StockService stockService;

    public ConsumeController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<ConsumptionResult> consume(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ConsumeRequest request) {
        ConsumptionReason reason = parseReason(request.reason());
        return ResponseEntity.ok(stockService.consume(parseUserId(jwt), request.stockItemId(), request.quantity(), reason));
    }

    private Long parseUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private ConsumptionReason parseReason(String value) {
        if (value == null || value.isBlank()) {
            return ConsumptionReason.CONSUME;
        }
        try {
            return ConsumptionReason.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid reason: " + value);
        }
    }

    public record ConsumeRequest(@NotNull Long stockItemId, @Positive int quantity, String reason) {
    }
}
