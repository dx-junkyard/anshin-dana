package com.anshindana.service;

import com.anshindana.domain.ConsumptionLog;
import com.anshindana.domain.ConsumptionLogRepository;
import com.anshindana.domain.ConsumptionReason;
import com.anshindana.domain.Product;
import com.anshindana.domain.ProductCandidate;
import com.anshindana.domain.ProductRepository;
import com.anshindana.domain.StockItem;
import com.anshindana.domain.StockItemRepository;
import com.anshindana.domain.StockLot;
import com.anshindana.domain.StockLotRepository;
import com.anshindana.domain.User;
import com.anshindana.domain.UserRepository;
import com.anshindana.service.dto.ConsumptionResult;
import com.anshindana.service.dto.ProductSummary;
import com.anshindana.service.dto.StockItemView;
import com.anshindana.service.dto.StockLotView;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final ProductRepository productRepository;
    private final StockItemRepository stockItemRepository;
    private final StockLotRepository stockLotRepository;
    private final ConsumptionLogRepository consumptionLogRepository;
    private final UserRepository userRepository;

    public StockService(ProductRepository productRepository,
                        StockItemRepository stockItemRepository,
                        StockLotRepository stockLotRepository,
                        ConsumptionLogRepository consumptionLogRepository,
                        UserRepository userRepository) {
        this.productRepository = productRepository;
        this.stockItemRepository = stockItemRepository;
        this.stockLotRepository = stockLotRepository;
        this.consumptionLogRepository = consumptionLogRepository;
        this.userRepository = userRepository;
    }

    public Optional<ProductCandidate> findProductByBarcode(String barcode) {
        if (!StringUtils.hasText(barcode)) {
            return Optional.empty();
        }
        return productRepository.findByBarcode(barcode)
                .map(product -> new ProductCandidate(
                        product.getId(),
                        product.getBarcode(),
                        product.getName(),
                        product.getBrand(),
                        product.getDefaultCategory()
                ));
    }

    public List<String> lastUsedExpiryTemplates(Long userId, String barcode) {
        if (!StringUtils.hasText(barcode)) {
            return List.of();
        }
        return stockLotRepository.findRecentExpiryDates(userId, barcode, PageRequest.of(0, 3))
                .stream()
                .map(LocalDate::toString)
                .toList();
    }

    @Transactional
    public StockItemView registerStock(Long userId, String barcode, String name, String brand, String category,
                                       int quantity, String unit, LocalDate expiresOn, LocalDate purchasedOn) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }
        User user = getUser(userId);
        Product product = findOrCreateProduct(barcode, name, brand, category);

        StockItem stockItem = stockItemRepository.findByUser_IdAndProduct_Id(userId, product.getId())
                .orElseGet(() -> new StockItem(user, product, unit, 0));
        stockItem.setUnit(unit);

        StockLot lot = new StockLot(quantity, expiresOn, purchasedOn);
        stockItem.addLot(lot);
        stockItem.setTotalQuantity(calculateTotalQuantity(stockItem.getLots()));

        StockItem saved = stockItemRepository.save(stockItem);
        return toView(saved);
    }

    public List<StockItemView> listStocks(Long userId, String sort) {
        List<StockItem> items = stockItemRepository.findAllWithProductAndLots(userId);
        List<StockItemView> views = items.stream().map(this::toView).toList();

        if ("expiresSoon".equalsIgnoreCase(sort)) {
            return views.stream()
                    .sorted(Comparator.comparing((StockItemView v) -> v.nextLot() != null ? v.nextLot().expiresOn() : LocalDate.MAX)
                            .thenComparing(v -> v.product().name()))
                    .toList();
        }
        return views.stream()
                .sorted(Comparator.comparing(v -> v.product().name()))
                .toList();
    }

    @Transactional
    public ConsumptionResult consume(Long userId, Long stockItemId, int quantity, ConsumptionReason reason) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }
        StockItem stockItem = stockItemRepository.findByIdAndUser_Id(stockItemId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock item not found"));

        List<StockLot> lots = stockLotRepository.findByStockItemIdOrderByExpiresOnAscIdAsc(stockItemId);
        List<StockLotView> consumedLots = new ArrayList<>();
        int remaining = quantity;

        for (StockLot lot : lots) {
            if (remaining <= 0) {
                break;
            }
            if (lot.getQuantity() <= 0) {
                continue;
            }
            int consumeQty = Math.min(remaining, lot.getQuantity());
            lot.setQuantity(lot.getQuantity() - consumeQty);
            consumedLots.add(new StockLotView(lot.getId(), lot.getExpiresOn(), consumeQty, lot.getPurchasedOn()));
            consumptionLogRepository.save(new ConsumptionLog(stockItem.getUser(), lot, -consumeQty, reason));
            remaining -= consumeQty;
        }

        if (remaining > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock to consume");
        }

        stockItem.setTotalQuantity(calculateTotalQuantity(lots));
        stockItemRepository.save(stockItem);

        return new ConsumptionResult(consumedLots, stockItem.getTotalQuantity());
    }

    private Product findOrCreateProduct(String barcode, String name, String brand, String category) {
        Optional<Product> existing = StringUtils.hasText(barcode)
                ? productRepository.findByBarcode(barcode).or(() -> productRepository.findFirstByNameIgnoreCase(name))
                : productRepository.findFirstByNameIgnoreCase(name);
        Product product = existing.orElseGet(() -> new Product(barcode, name, brand, category, null));

        if (StringUtils.hasText(name)) {
            product.setName(name);
        }
        if (StringUtils.hasText(barcode) && !StringUtils.hasText(product.getBarcode())) {
            product.setBarcode(barcode);
        }
        if (brand != null) {
            product.setBrand(brand);
        }
        if (category != null) {
            product.setDefaultCategory(category);
        }

        return productRepository.save(product);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private StockItemView toView(StockItem stockItem) {
        StockLot nextLot = stockItem.getLots().stream()
                .filter(lot -> lot.getQuantity() > 0)
                .min(Comparator.comparing(StockLot::getExpiresOn).thenComparing(StockLot::getId))
                .orElse(null);
        StockLotView nextLotView = nextLot != null
                ? new StockLotView(nextLot.getId(), nextLot.getExpiresOn(), nextLot.getQuantity(), nextLot.getPurchasedOn())
                : null;

        Product product = stockItem.getProduct();
        ProductSummary summary = new ProductSummary(product.getId(), product.getBarcode(), product.getName(), product.getBrand(), product.getDefaultCategory());

        return new StockItemView(stockItem.getId(), summary, stockItem.getUnit(), stockItem.getTotalQuantity(), nextLotView);
    }

    private int calculateTotalQuantity(List<StockLot> lots) {
        return lots.stream().mapToInt(StockLot::getQuantity).sum();
    }
}
