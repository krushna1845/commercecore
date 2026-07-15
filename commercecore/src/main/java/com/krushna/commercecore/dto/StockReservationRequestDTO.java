package com.krushna.commercecore.dto;

public class StockReservationRequestDTO {
    private Long productId;
    private Long variantId; // Optional
    private int quantity;
    private String referenceId; // Order ID

    public StockReservationRequestDTO() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}
