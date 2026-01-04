package com.anshindana.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "consumption_logs")
public class ConsumptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_lot_id")
    private StockLot stockLot;

    @Column(name = "delta_quantity", nullable = false)
    private int deltaQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 32)
    private ConsumptionReason reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ConsumptionLog() {
    }

    public ConsumptionLog(User user, StockLot stockLot, int deltaQuantity, ConsumptionReason reason) {
        this.user = user;
        this.stockLot = stockLot;
        this.deltaQuantity = deltaQuantity;
        this.reason = reason;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public StockLot getStockLot() {
        return stockLot;
    }

    public int getDeltaQuantity() {
        return deltaQuantity;
    }

    public ConsumptionReason getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
