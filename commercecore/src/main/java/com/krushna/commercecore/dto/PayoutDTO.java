package com.krushna.commercecore.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PayoutDTO {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private double amount;
    private String status;
    private String referenceNumber;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
