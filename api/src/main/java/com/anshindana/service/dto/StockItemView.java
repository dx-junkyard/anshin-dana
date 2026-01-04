package com.anshindana.service.dto;

public record StockItemView(Long id, ProductSummary product, String unit, int totalQuantity, StockLotView nextLot) {
}
