package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private double price;
    private double originalPrice;
    private boolean inStock;
    private LocalDateTime addedAt;
    private boolean priceDropAlert;
    private boolean stockAlert;
    private double alertPrice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
    public boolean isPriceDropAlert() { return priceDropAlert; }
    public void setPriceDropAlert(boolean priceDropAlert) { this.priceDropAlert = priceDropAlert; }
    public boolean isStockAlert() { return stockAlert; }
    public void setStockAlert(boolean stockAlert) { this.stockAlert = stockAlert; }
    public double getAlertPrice() { return alertPrice; }
    public void setAlertPrice(double alertPrice) { this.alertPrice = alertPrice; }
}
