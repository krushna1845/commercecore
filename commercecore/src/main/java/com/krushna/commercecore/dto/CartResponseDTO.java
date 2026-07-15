package com.krushna.commercecore.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {

    private List<CartItemResponseDTO> items;
    private BigDecimal totalPrice;

    public CartResponseDTO() {}

    public CartResponseDTO(List<CartItemResponseDTO> items, BigDecimal totalPrice) {
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public List<CartItemResponseDTO> getItems() { return items; }
    public void setItems(List<CartItemResponseDTO> items) { this.items = items; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}
