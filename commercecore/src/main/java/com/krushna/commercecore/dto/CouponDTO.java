package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {
    private Long id;
    private String code;
    private String discountType;
    private double discountValue;
    private double minPurchase;
    private double maxDiscount;
    private boolean active;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
}
