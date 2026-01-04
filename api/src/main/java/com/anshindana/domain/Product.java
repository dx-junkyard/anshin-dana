package com.anshindana.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = "barcode"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "barcode", unique = true, length = 64)
    private String barcode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "default_category")
    private String defaultCategory;

    @Column(name = "nutrition_json", columnDefinition = "jsonb")
    private String nutritionJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Product() {
    }

    public Product(String barcode, String name, String brand, String defaultCategory, String nutritionJson) {
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
        this.defaultCategory = defaultCategory;
        this.nutritionJson = nutritionJson;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public String getNutritionJson() {
        return nutritionJson;
    }

    public void setNutritionJson(String nutritionJson) {
        this.nutritionJson = nutritionJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
