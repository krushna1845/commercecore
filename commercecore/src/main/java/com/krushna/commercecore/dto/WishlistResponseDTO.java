package com.krushna.commercecore.dto;

import java.time.LocalDateTime;

public class WishlistResponseDTO {

    private Long userId;
    private Long productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private String productImageUrl;
    private boolean productInStock;
    private LocalDateTime addedAt;

    public WishlistResponseDTO() {}

    public WishlistResponseDTO(Long userId, Long productId, String productName, 
                               String productDescription, Double productPrice, 
                               String productImageUrl, boolean productInStock, 
                               LocalDateTime addedAt) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.productInStock = productInStock;
        this.addedAt = addedAt;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public Double getProductPrice() { return productPrice; }
    public void setProductPrice(Double productPrice) { this.productPrice = productPrice; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public boolean isProductInStock() { return productInStock; }
    public void setProductInStock(boolean productInStock) { this.productInStock = productInStock; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
