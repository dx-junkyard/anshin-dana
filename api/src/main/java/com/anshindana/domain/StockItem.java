package com.anshindana.domain;

public record StockItem(Long id, String name, String category, String unit, int totalQuantity, StockLot nextLot) {
}
