package com.anshindana.domain;

import java.time.LocalDate;

public record StockLot(Long id, LocalDate expiresOn, int quantity, LocalDate purchasedOn) {
}
