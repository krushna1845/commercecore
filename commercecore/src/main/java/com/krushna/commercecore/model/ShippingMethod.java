package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_methods")
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double basePrice;

    @Column(nullable = false)
    private double pricePerKg;

    @Column(nullable = false)
    private int estimatedDaysMin;

    @Column(nullable = false)
    private int estimatedDaysMax;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public ShippingMethod() {}

    public ShippingMethod(String name, String description, double basePrice, double pricePerKg, int estimatedDaysMin, int estimatedDaysMax) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.pricePerKg = pricePerKg;
        this.estimatedDaysMin = estimatedDaysMin;
        this.estimatedDaysMax = estimatedDaysMax;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }

    public int getEstimatedDaysMin() { return estimatedDaysMin; }
    public void setEstimatedDaysMin(int estimatedDaysMin) { this.estimatedDaysMin = estimatedDaysMin; }

    public int getEstimatedDaysMax() { return estimatedDaysMax; }
    public void setEstimatedDaysMax(int estimatedDaysMax) { this.estimatedDaysMax = estimatedDaysMax; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
