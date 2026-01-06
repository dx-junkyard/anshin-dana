package com.anshindana.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

    List<StockLot> findByStockItemIdOrderByExpiresOnAscIdAsc(Long stockItemId);

    @Query("""
            SELECT sl.expiresOn FROM StockLot sl
            JOIN sl.stockItem si
            JOIN si.product p
            WHERE si.user.id = :userId AND p.barcode = :barcode
            ORDER BY sl.expiresOn DESC, sl.id DESC
            """)
    List<LocalDate> findRecentExpiryDates(Long userId, String barcode, Pageable pageable);
}
