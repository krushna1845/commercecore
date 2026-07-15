package com.krushna.commercecore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 20, message = "Coupon code must be between 3 and 20 characters")
    @Column(unique = true, nullable = false, updatable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Min(value = 0, message = "Discount percentage cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    @Column(nullable = false)
    private int discountPercentage = 0;

    @Min(value = 0, message = "Discount amount cannot be negative")
    @Column(nullable = false)
    private double discountAmount = 0.0;

    @Min(value = 0, message = "Minimum purchase amount cannot be negative")
    @Column(nullable = false)
    private double minPurchaseAmount = 0.0;

    @Future(message = "Expiry date must be in the future")
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private int usageLimit = 0; // 0 means unlimited

    @Column(nullable = false)
    private int timesUsed = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // Convert code to uppercase for consistency
        if (this.code != null) {
            this.code = this.code.toUpperCase();
        }
    }

    public Coupon() {}

    public Coupon(String code, DiscountType discountType, int discountPercentage, double discountAmount, LocalDateTime expiryDate) {
        this.code = code;
        this.discountType = discountType;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.expiryDate = expiryDate;
    }

    // Business methods
    public boolean isValid() {
        return isActive && 
               LocalDateTime.now().isBefore(expiryDate) && 
               (usageLimit == 0 || timesUsed < usageLimit);
    }

    public boolean canBeUsed() {
        return isValid() && (usageLimit == 0 || timesUsed < usageLimit);
    }

    public void incrementUsage() {
        this.timesUsed++;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { 
        this.code = code != null ? code.toUpperCase() : code;
    }

    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }

    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getMinPurchaseAmount() { return minPurchaseAmount; }
    public void setMinPurchaseAmount(double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getUsageLimit() { return usageLimit; }
    public void setUsageLimit(int usageLimit) { this.usageLimit = usageLimit; }

    public int getTimesUsed() { return timesUsed; }
    public void setTimesUsed(int timesUsed) { this.timesUsed = timesUsed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
