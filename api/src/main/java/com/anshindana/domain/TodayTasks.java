package com.anshindana.domain;

import java.util.List;

public record TodayTasks(
        List<ExpiringItem> expiringSoon,
        List<ExpiredItem> expired,
        List<LowStockItem> lowStock,
        List<SuggestedConsume> suggestedConsume
) {
    public record ExpiringItem(String name, String expiresOn) {
    }

    public record ExpiredItem(String name, String expiredOn) {
    }

    public record LowStockItem(String category, String suggestion) {
    }

    public record SuggestedConsume(String name, String reason) {
    }
}
