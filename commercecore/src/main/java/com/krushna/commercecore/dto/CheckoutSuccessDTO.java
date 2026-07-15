package com.krushna.commercecore.dto;

import com.krushna.commercecore.model.Order;
import java.math.BigDecimal;

public class CheckoutSuccessDTO {
    
    private OrderSummary order;
    private String clientSecret;
    private String paymentIntentId;

    public CheckoutSuccessDTO() {}

    public CheckoutSuccessDTO(Order order, String clientSecret, String paymentIntentId) {
        this.order = new OrderSummary(order.getId(), order.getTotalAmount());
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
    }

    public OrderSummary getOrder() { return order; }
    public void setOrder(OrderSummary order) { this.order = order; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
    
    public static class OrderSummary {
        private Long id;
        private BigDecimal totalAmount;
        
        public OrderSummary() {}
        public OrderSummary(Long id, BigDecimal totalAmount) {
            this.id = id;
            this.totalAmount = totalAmount;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    }
}
