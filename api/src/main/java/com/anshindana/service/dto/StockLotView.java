package com.anshindana.service.dto;

import java.time.LocalDate;

public record StockLotView(Long id, LocalDate expiresOn, int quantity, LocalDate purchasedOn) {
}
