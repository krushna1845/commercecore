package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private double subtotal;
    private double shippingCost;
    private double giftWrapCost;
    private double deliverySlotCost;
    private double discount;
    private double gst;
    private double total;
    private List<OrderItemSummaryDTO> items;
    private String appliedCouponCode;
    private double couponDiscount;

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }
    public double getGiftWrapCost() { return giftWrapCost; }
    public void setGiftWrapCost(double giftWrapCost) { this.giftWrapCost = giftWrapCost; }
    public double getDeliverySlotCost() { return deliverySlotCost; }
    public void setDeliverySlotCost(double deliverySlotCost) { this.deliverySlotCost = deliverySlotCost; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getGst() { return gst; }
    public void setGst(double gst) { this.gst = gst; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<OrderItemSummaryDTO> getItems() { return items; }
    public void setItems(List<OrderItemSummaryDTO> items) { this.items = items; }
    public String getAppliedCouponCode() { return appliedCouponCode; }
    public void setAppliedCouponCode(String appliedCouponCode) { this.appliedCouponCode = appliedCouponCode; }
    public double getCouponDiscount() { return couponDiscount; }
    public void setCouponDiscount(double couponDiscount) { this.couponDiscount = couponDiscount; }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemSummaryDTO {
        private Long productId;
        private String productName;
        private Integer quantity;
        private double price;
        private double totalPrice;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    }
}
