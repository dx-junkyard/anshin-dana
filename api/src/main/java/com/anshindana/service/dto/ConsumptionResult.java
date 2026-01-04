package com.anshindana.service.dto;

import java.util.List;

public record ConsumptionResult(List<StockLotView> consumedLots, int remainingTotal) {
}
