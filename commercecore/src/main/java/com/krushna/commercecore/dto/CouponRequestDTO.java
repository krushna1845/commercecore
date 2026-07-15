package com.krushna.commercecore.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CouponRequestDTO {

    @NotBlank(message = "Coupon code is required")
    @Size(min = 3, max = 20, message = "Coupon code must be between 3 and 20 characters")
    private String code;

    private String discountType = "PERCENTAGE";

    @Min(value = 0, message = "Discount percentage cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer discountPercentage = 0;

    @Min(value = 0, message = "Discount amount cannot be negative")
    private Double discountAmount = 0.0;

    @Min(value = 0, message = "Minimum purchase amount cannot be negative")
    private Double minPurchaseAmount = 0.0;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;

    private boolean isActive = true;

    private Integer usageLimit = 0; // 0 means unlimited

    public CouponRequestDTO() {}

    public CouponRequestDTO(String code, String discountType, Integer discountPercentage, Double discountAmount, Double minPurchaseAmount, LocalDateTime expiryDate) {
        this.code = code;
        this.discountType = discountType;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.minPurchaseAmount = minPurchaseAmount;
        this.expiryDate = expiryDate;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Integer discountPercentage) { this.discountPercentage = discountPercentage; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getMinPurchaseAmount() { return minPurchaseAmount; }
    public void setMinPurchaseAmount(Double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }
}
