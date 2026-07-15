package com.krushna.commercecore.dto;

import java.time.LocalDateTime;

public class CouponResponseDTO {

    private Long id;
    private String code;
    private String discountType;
    private Integer discountPercentage;
    private Double discountAmount;
    private Double minPurchaseAmount;
    private LocalDateTime expiryDate;
    private boolean isActive;
    private Integer usageLimit;
    private Integer timesUsed;
    private LocalDateTime createdAt;
    private boolean isValid;

    public CouponResponseDTO() {}

    public CouponResponseDTO(Long id, String code, String discountType, Integer discountPercentage, 
                             Double discountAmount, Double minPurchaseAmount,
                             LocalDateTime expiryDate, boolean isActive, 
                             Integer usageLimit, Integer timesUsed, 
                             LocalDateTime createdAt, boolean isValid) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.minPurchaseAmount = minPurchaseAmount;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
        this.usageLimit = usageLimit;
        this.timesUsed = timesUsed;
        this.createdAt = createdAt;
        this.isValid = isValid;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Integer getTimesUsed() { return timesUsed; }
    public void setTimesUsed(Integer timesUsed) { this.timesUsed = timesUsed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }

    public Integer getRemainingUses() {
        if (usageLimit == 0) return null; // Unlimited
        return Math.max(0, usageLimit - timesUsed);
    }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getMinPurchaseAmount() { return minPurchaseAmount; }
    public void setMinPurchaseAmount(Double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }
}
