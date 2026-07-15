package com.krushna.commercecore.dto;

import java.math.BigDecimal;

public class PaymentIntentResponseDTO {

    private String clientSecret;
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
    private Long orderId;

    public PaymentIntentResponseDTO() {}

    public PaymentIntentResponseDTO(String clientSecret, String paymentIntentId,
                                    BigDecimal amount, String currency, Long orderId) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
    }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
