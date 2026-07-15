package com.krushna.commercecore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreatePaymentIntentRequestDTO {

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.50", message = "Minimum charge amount is 0.50")
    private BigDecimal amount;

    @NotBlank(message = "Currency must not be blank")
    private String currency;

    public CreatePaymentIntentRequestDTO() {}

    public CreatePaymentIntentRequestDTO(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
