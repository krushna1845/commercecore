package com.krushna.commercecore.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartPreviewDTO {
    private List<SellerOrderPreviewDTO> sellerOrders;
    private BigDecimal subtotal;
    private BigDecimal shippingTotal;
    private BigDecimal taxTotal;
    private BigDecimal discountTotal;
    private BigDecimal grandTotal;

    public CartPreviewDTO() {}

    public CartPreviewDTO(List<SellerOrderPreviewDTO> sellerOrders, BigDecimal subtotal, BigDecimal shippingTotal,
                          BigDecimal taxTotal, BigDecimal discountTotal, BigDecimal grandTotal) {
        this.sellerOrders = sellerOrders;
        this.subtotal = subtotal;
        this.shippingTotal = shippingTotal;
        this.taxTotal = taxTotal;
        this.discountTotal = discountTotal;
        this.grandTotal = grandTotal;
    }

    public List<SellerOrderPreviewDTO> getSellerOrders() { return sellerOrders; }
    public void setSellerOrders(List<SellerOrderPreviewDTO> sellerOrders) { this.sellerOrders = sellerOrders; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getShippingTotal() { return shippingTotal; }
    public void setShippingTotal(BigDecimal shippingTotal) { this.shippingTotal = shippingTotal; }

    public BigDecimal getTaxTotal() { return taxTotal; }
    public void setTaxTotal(BigDecimal taxTotal) { this.taxTotal = taxTotal; }

    public BigDecimal getDiscountTotal() { return discountTotal; }
    public void setDiscountTotal(BigDecimal discountTotal) { this.discountTotal = discountTotal; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
}
