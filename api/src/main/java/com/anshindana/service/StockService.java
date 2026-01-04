package com.anshindana.service;

import com.anshindana.domain.ProductCandidate;
import com.anshindana.domain.StockItem;
import com.anshindana.domain.StockLot;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {

    public ProductCandidate findProductByBarcode(String barcode) {
        return new ProductCandidate(barcode, "Mock Beans", "Sample Brand", "canned");
    }

    public List<String> lastUsedExpiryTemplates(Long userId, String barcode) {
        return List.of("2025-06-01", "2025-12-01");
    }

    public StockItem registerStock(String name, String category, int quantity, String unit, LocalDate expiresOn) {
        StockLot lot = new StockLot(10L, expiresOn, quantity, LocalDate.now());
        return new StockItem(5L, name, category, unit, quantity, lot);
    }

    public List<StockItem> listStocks() {
        return List.of(
                new StockItem(1L, "パスタソース", "sauce", "袋", 5, new StockLot(1L, LocalDate.now().plusDays(30), 2, LocalDate.now().minusDays(3))),
                new StockItem(2L, "ツナ缶", "canned", "缶", 8, new StockLot(2L, LocalDate.now().plusDays(40), 3, LocalDate.now().minusDays(10)))
        );
    }

    public ConsumptionResult consume(Long stockItemId, int quantity) {
        List<StockLot> consumed = List.of(
                new StockLot(1L, LocalDate.now().plusDays(10), 1, LocalDate.now().minusDays(15)),
                new StockLot(2L, LocalDate.now().plusDays(30), quantity - 1, LocalDate.now().minusDays(20))
        );
        return new ConsumptionResult(consumed, 3);
    }

    public record ConsumptionResult(List<StockLot> consumedLots, int remainingTotal) {
    }
}
