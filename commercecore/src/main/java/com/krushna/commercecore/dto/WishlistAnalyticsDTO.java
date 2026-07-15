package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class WishlistAnalyticsDTO {
    private Long productId;
    private String productName;
    private int viewCount;
    private int moveToCartCount;
    private LocalDateTime lastViewedAt;
    private LocalDateTime lastMovedToCartAt;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getMoveToCartCount() { return moveToCartCount; }
    public void setMoveToCartCount(int moveToCartCount) { this.moveToCartCount = moveToCartCount; }
    public LocalDateTime getLastViewedAt() { return lastViewedAt; }
    public void setLastViewedAt(LocalDateTime lastViewedAt) { this.lastViewedAt = lastViewedAt; }
    public LocalDateTime getLastMovedToCartAt() { return lastMovedToCartAt; }
    public void setLastMovedToCartAt(LocalDateTime lastMovedToCartAt) { this.lastMovedToCartAt = lastMovedToCartAt; }
}
