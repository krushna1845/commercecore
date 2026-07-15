package com.krushna.commercecore.dto;

import java.math.BigDecimal;
import java.util.List;

public class SellerOrderPreviewDTO {
    private Long sellerId;
    private String sellerUsername;
    private List<CartItemResponseDTO> items;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal total;

    public SellerOrderPreviewDTO() {}

    public SellerOrderPreviewDTO(Long sellerId, String sellerUsername, List<CartItemResponseDTO> items,
                                 BigDecimal subtotal, BigDecimal shippingCost, BigDecimal taxAmount, BigDecimal total) {
        this.sellerId = sellerId;
        this.sellerUsername = sellerUsername;
        this.items = items;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.taxAmount = taxAmount;
        this.total = total;
    }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }

    public List<CartItemResponseDTO> getItems() { return items; }
    public void setItems(List<CartItemResponseDTO> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
