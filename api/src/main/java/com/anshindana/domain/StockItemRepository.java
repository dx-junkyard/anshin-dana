package com.anshindana.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    Optional<StockItem> findByUser_IdAndProduct_Id(Long userId, Long productId);

    Optional<StockItem> findByIdAndUser_Id(Long id, Long userId);

    @EntityGraph(attributePaths = {"product", "lots"})
    @Query("SELECT DISTINCT si FROM StockItem si WHERE si.user.id = :userId")
    List<StockItem> findAllWithProductAndLots(Long userId);
}
