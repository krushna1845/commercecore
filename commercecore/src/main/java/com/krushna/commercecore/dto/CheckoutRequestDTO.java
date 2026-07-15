package com.krushna.commercecore.dto;

import java.util.List;

public class CheckoutRequestDTO {
    private Long shippingAddressId;
    private Long billingAddressId;
    private Long deliverySlotId;
    private Long shippingMethodId;
    private Long giftWrapId;
    private String couponCode;
    private boolean gstInvoice;
    private String gstNumber;
    private String giftMessage;
    private List<CartItemDTO> items;
    private String paymentMethod;

    public CheckoutRequestDTO() {}

    public CheckoutRequestDTO(Long shippingAddressId, Long billingAddressId, Long deliverySlotId, Long shippingMethodId, Long giftWrapId, String couponCode, boolean gstInvoice, String gstNumber, String giftMessage, List<CartItemDTO> items, String paymentMethod) {
        this.shippingAddressId = shippingAddressId;
        this.billingAddressId = billingAddressId;
        this.deliverySlotId = deliverySlotId;
        this.shippingMethodId = shippingMethodId;
        this.giftWrapId = giftWrapId;
        this.couponCode = couponCode;
        this.gstInvoice = gstInvoice;
        this.gstNumber = gstNumber;
        this.giftMessage = giftMessage;
        this.items = items;
        this.paymentMethod = paymentMethod;
    }

    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public Long getBillingAddressId() { return billingAddressId; }
    public void setBillingAddressId(Long billingAddressId) { this.billingAddressId = billingAddressId; }
    public Long getDeliverySlotId() { return deliverySlotId; }
    public void setDeliverySlotId(Long deliverySlotId) { this.deliverySlotId = deliverySlotId; }
    public Long getShippingMethodId() { return shippingMethodId; }
    public void setShippingMethodId(Long shippingMethodId) { this.shippingMethodId = shippingMethodId; }
    public Long getGiftWrapId() { return giftWrapId; }
    public void setGiftWrapId(Long giftWrapId) { this.giftWrapId = giftWrapId; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public boolean isGstInvoice() { return gstInvoice; }
    public void setGstInvoice(boolean gstInvoice) { this.gstInvoice = gstInvoice; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public String getGiftMessage() { return giftMessage; }
    public void setGiftMessage(String giftMessage) { this.giftMessage = giftMessage; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public static class CartItemDTO {
        private Long productId;
        private Integer quantity;

        public CartItemDTO() {}

        public CartItemDTO(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
