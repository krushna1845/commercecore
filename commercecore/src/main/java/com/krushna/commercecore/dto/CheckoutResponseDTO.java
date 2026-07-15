package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponseDTO {
    private OrderSummaryDTO orderSummary;
    private List<ShippingMethodDTO> shippingMethods;
    private List<DeliverySlotDTO> deliverySlots;
    private List<GiftWrapDTO> giftWraps;
    private CouponDTO appliedCoupon;
    private AddressDTO shippingAddress;
    private AddressDTO billingAddress;
    private boolean gstInvoice;
    private String gstNumber;

    public OrderSummaryDTO getOrderSummary() { return orderSummary; }
    public void setOrderSummary(OrderSummaryDTO orderSummary) { this.orderSummary = orderSummary; }
    public List<ShippingMethodDTO> getShippingMethods() { return shippingMethods; }
    public void setShippingMethods(List<ShippingMethodDTO> shippingMethods) { this.shippingMethods = shippingMethods; }
    public List<DeliverySlotDTO> getDeliverySlots() { return deliverySlots; }
    public void setDeliverySlots(List<DeliverySlotDTO> deliverySlots) { this.deliverySlots = deliverySlots; }
    public List<GiftWrapDTO> getGiftWraps() { return giftWraps; }
    public void setGiftWraps(List<GiftWrapDTO> giftWraps) { this.giftWraps = giftWraps; }
    public CouponDTO getAppliedCoupon() { return appliedCoupon; }
    public void setAppliedCoupon(CouponDTO appliedCoupon) { this.appliedCoupon = appliedCoupon; }
    public AddressDTO getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(AddressDTO shippingAddress) { this.shippingAddress = shippingAddress; }
    public AddressDTO getBillingAddress() { return billingAddress; }
    public void setBillingAddress(AddressDTO billingAddress) { this.billingAddress = billingAddress; }
    public boolean isGstInvoice() { return gstInvoice; }
    public void setGstInvoice(boolean gstInvoice) { this.gstInvoice = gstInvoice; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
}
